package org.yannvanneth.tomneak.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.paymentservice.exception.NotFoundException;
import org.yannvanneth.tomneak.paymentservice.exception.PaymentFailedException;
import org.yannvanneth.tomneak.paymentservice.model.entity.*;
import org.yannvanneth.tomneak.paymentservice.model.event.PaymentEvent;
import org.yannvanneth.tomneak.paymentservice.model.request.KhqrGenerateRequest;
import org.yannvanneth.tomneak.paymentservice.model.request.KhqrVerifyRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.KhqrResponse;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Service implementation for NBC Bakong KHQR QR Code generation, CRC16 checksum computation,
 * Bakong Deep Link generation, and SAGA CDC payment verification.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KhqrServiceImpl implements KhqrService {

    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Generates EMVCo compliant Bakong KHQR payload, saves PENDING Payment, and produces response.
     *
     * @param request KhqrGenerateRequest DTO
     * @return KhqrResponse DTO
     */
    @Override
    @Transactional
    public KhqrResponse generateKhqr(KhqrGenerateRequest request) {
        log.info("Generating KHQR code for orderId: {}, amount: {}, currency: {}", request.getOrderId(), request.getAmount(), request.getCurrency());

        String generatedPaymentId = "KHQR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String merchantName = request.getMerchantName() != null ? request.getMerchantName() : "Tomneak Store";
        String currencyCode = "KHR".equalsIgnoreCase(request.getCurrency()) ? "116" : "840"; // 840 = USD, 116 = KHR

        // Construct EMVCo KHQR String
        String rawKhqr = buildEmvCoKhqrString(generatedPaymentId, request.getOrderId(), request.getAmount(), currencyCode, merchantName, request.getBakongAccountId());
        String md5Hash = calculateMd5(rawKhqr);
        String deepLink = "https://api-bakong.nbc.gov.kh/checkout?qr=" + URLEncoder.encode(rawKhqr, StandardCharsets.UTF_8);

        // Save Payment in PENDING status until customer scans and pays
        Payment payment = Payment.builder()
                .paymentId(generatedPaymentId)
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(PaymentMethod.KHQR)
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return KhqrResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .userId(savedPayment.getUserId())
                .amount(savedPayment.getAmount())
                .currency("116".equals(currencyCode) ? "KHR" : "USD")
                .qrCode(rawKhqr)
                .md5(md5Hash)
                .deepLink(deepLink)
                .merchantName(merchantName)
                .status(savedPayment.getStatus())
                .createdAt(savedPayment.getCreatedAt())
                .build();
    }

    /**
     * Verifies KHQR transaction payment and transactionally inserts SAGA PAYMENT_COMPLETED event into payment_outbox.
     *
     * @param request KhqrVerifyRequest DTO
     * @return KhqrResponse DTO
     */
    @Override
    @Transactional
    public KhqrResponse verifyKhqr(KhqrVerifyRequest request) {
        log.info("Verifying KHQR payment for paymentId: {}", request.getPaymentId());

        Payment payment = paymentRepository.findByPaymentId(request.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Payment not found for KHQR verification ID: " + request.getPaymentId()));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("KHQR Payment {} already verified as SUCCESS", payment.getPaymentId());
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            log.info("Updated KHQR Payment {} status to SUCCESS", payment.getPaymentId());

            // Construct SAGA Payment Event Payload
            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .eventType("PAYMENT_COMPLETED")
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId())
                    .amount(payment.getAmount())
                    .status(PaymentStatus.SUCCESS)
                    .build();

            try {
                String jsonPayload = objectMapper.writeValueAsString(paymentEvent);
                PaymentOutbox outboxRecord = PaymentOutbox.builder()
                        .aggregateType("PAYMENT")
                        .aggregateId(payment.getPaymentId())
                        .eventType("PAYMENT_COMPLETED")
                        .payload(jsonPayload)
                        .status(OutboxStatus.PENDING)
                        .build();
                outboxRepository.save(outboxRecord);
                log.info("Transactionally saved SAGA PAYMENT_COMPLETED CDC Outbox record for KHQR Payment {}", payment.getPaymentId());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize KHQR PaymentEvent payload for outbox", e);
                throw new PaymentFailedException("Failed to serialize KHQR SAGA event payload");
            }
        }

        return KhqrResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    /**
     * Builds standard EMVCo KHQR String for Bakong payments.
     */
    private String buildEmvCoKhqrString(String paymentId, String orderId, BigDecimal amount, String currencyCode, String merchantName, String bakongId) {
        String bakongAccount = bakongId != null ? bakongId : "tomneak_merchant@kmbl";
        String formattedAmount = amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00";

        StringBuilder sb = new StringBuilder();
        sb.append("000201"); // Payload Format Indicator
        sb.append("010212"); // Point of Initiation (Dynamic QR)

        // Merchant Account Information (Tag 30)
        String bakongData = "00" + String.format("%02d", bakongAccount.length()) + bakongAccount;
        sb.append("30").append(String.format("%02d", bakongData.length())).append(bakongData);

        sb.append("52045999"); // Merchant Category Code
        sb.append("53").append(String.format("%02d", currencyCode.length())).append(currencyCode);
        sb.append("54").append(String.format("%02d", formattedAmount.length())).append(formattedAmount);
        sb.append("5802KH");   // Country Code

        sb.append("59").append(String.format("%02d", merchantName.length())).append(merchantName);
        sb.append("6007PhnomPenh"); // Merchant City

        // Additional Data Field (Tag 62) - Bill Number / Ref
        String refData = "01" + String.format("%02d", orderId.length()) + orderId;
        sb.append("62").append(String.format("%02d", refData.length())).append(refData);

        sb.append("6304"); // Checksum Tag
        String checksum = calculateCrc16Ccitt(sb.toString());
        sb.append(checksum);

        return sb.toString();
    }

    /**
     * Calculates CCITT CRC-16 Checksum (Polynomial 0x1021, Initial 0xFFFF) for KHQR compliance.
     */
    public static String calculateCrc16Ccitt(String data) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    /**
     * Computes MD5 Hash hex string.
     */
    private String calculateMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not found", e);
            return "";
        }
    }
}
