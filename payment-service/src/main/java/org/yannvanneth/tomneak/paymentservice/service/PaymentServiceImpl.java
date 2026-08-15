package org.yannvanneth.tomneak.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.paymentservice.exception.NotFoundException;
import org.yannvanneth.tomneak.paymentservice.exception.PaymentFailedException;
import org.yannvanneth.tomneak.paymentservice.model.entity.*;
import org.yannvanneth.tomneak.paymentservice.model.event.PaymentEvent;
import org.yannvanneth.tomneak.paymentservice.model.request.PaymentRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.PaymentResponse;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Payment operations incorporating SAGA orchestration and CDC Transactional Outbox pattern.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository outboxRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    /**
     * Processes a payment transaction and transactionally saves the payment entity and outbox CDC record.
     *
     * @param request PaymentRequest DTO
     * @return PaymentResponse DTO
     */
    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for orderId: {}, userId: {}, amount: {}", request.getOrderId(), request.getUserId(), request.getAmount());

        String generatedPaymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        boolean isSuccess = request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0;

        PaymentStatus status = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        String failureReason = isSuccess ? null : "Invalid payment amount specified";

        // Build Payment Entity
        Payment payment = Payment.builder()
                .paymentId(generatedPaymentId)
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(status)
                .failureReason(failureReason)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Saved Payment record in database with ID: {}", savedPayment.getPaymentId());

        // Construct SAGA Payment Event Payload
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .eventType(isSuccess ? "PAYMENT_COMPLETED" : "PAYMENT_FAILED")
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .userId(savedPayment.getUserId())
                .amount(savedPayment.getAmount())
                .status(savedPayment.getStatus())
                .failureReason(savedPayment.getFailureReason())
                .build();

        // Serialize event payload to JSON string for CDC Outbox table
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(paymentEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PaymentEvent payload", e);
            throw new PaymentFailedException("Failed to process payment event payload");
        }

        // Save CDC Transactional Outbox record in the same local DB transaction
        PaymentOutbox outboxRecord = PaymentOutbox.builder()
                .aggregateType("PAYMENT")
                .aggregateId(savedPayment.getPaymentId())
                .eventType(paymentEvent.getEventType())
                .payload(jsonPayload)
                .status(OutboxStatus.PENDING)
                .build();

        outboxRepository.save(outboxRecord);
        log.info("Saved CDC PaymentOutbox record for paymentId: {}", savedPayment.getPaymentId());

        return modelMapper.map(savedPayment, PaymentResponse.class);
    }

    /**
     * Retrieves a payment by paymentId.
     *
     * @param paymentId payment identifier
     * @return PaymentResponse DTO
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + paymentId));
        return modelMapper.map(payment, PaymentResponse.class);
    }

    /**
     * Retrieves all payments for an orderId.
     *
     * @param orderId order identifier
     * @return List of PaymentResponse DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(String orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Cancels or refunds a payment as a SAGA compensating transaction.
     *
     * @param paymentId payment identifier
     * @param reason cancellation reason
     * @return PaymentResponse DTO
     */
    @Override
    @Transactional
    public PaymentResponse cancelPayment(String paymentId, String reason) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + paymentId));

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason(reason);
        Payment updatedPayment = paymentRepository.save(payment);

        // Emit SAGA Compensating Event via CDC Outbox
        PaymentEvent cancelEvent = PaymentEvent.builder()
                .eventType("PAYMENT_CANCELLED")
                .paymentId(updatedPayment.getPaymentId())
                .orderId(updatedPayment.getOrderId())
                .userId(updatedPayment.getUserId())
                .amount(updatedPayment.getAmount())
                .status(PaymentStatus.REFUNDED)
                .failureReason(reason)
                .build();

        try {
            String jsonPayload = objectMapper.writeValueAsString(cancelEvent);
            PaymentOutbox outboxRecord = PaymentOutbox.builder()
                    .aggregateType("PAYMENT")
                    .aggregateId(updatedPayment.getPaymentId())
                    .eventType("PAYMENT_CANCELLED")
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .build();
            outboxRepository.save(outboxRecord);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize cancel payment event payload", e);
        }

        return modelMapper.map(updatedPayment, PaymentResponse.class);
    }
}
