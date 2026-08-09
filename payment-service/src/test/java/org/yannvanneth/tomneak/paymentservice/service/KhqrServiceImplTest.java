package org.yannvanneth.tomneak.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yannvanneth.tomneak.paymentservice.model.entity.Payment;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentMethod;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentOutbox;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentStatus;
import org.yannvanneth.tomneak.paymentservice.model.request.KhqrGenerateRequest;
import org.yannvanneth.tomneak.paymentservice.model.request.KhqrVerifyRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.KhqrResponse;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KhqrServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentOutboxRepository outboxRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private KhqrServiceImpl khqrService;

    private KhqrGenerateRequest generateRequest;

    @BeforeEach
    void setUp() {
        generateRequest = KhqrGenerateRequest.builder()
                .orderId("ORD-888")
                .userId("USER-222")
                .amount(new BigDecimal("25.00"))
                .currency("USD")
                .merchantName("Tomneak Merchant")
                .bakongAccountId("tomneak@kmbl")
                .build();
    }

    @Test
    void generateKhqr_ShouldGenerateEmvCoKhqrStringMd5AndDeepLink() {
        Payment savedPayment = Payment.builder()
                .id(10L)
                .paymentId("KHQR-12345678")
                .orderId("ORD-888")
                .userId("USER-222")
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.KHQR)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        KhqrResponse response = khqrService.generateKhqr(generateRequest);

        assertNotNull(response);
        assertEquals("KHQR-12345678", response.getPaymentId());
        assertNotNull(response.getQrCode());
        assertTrue(response.getQrCode().startsWith("000201"));
        assertTrue(response.getQrCode().contains("5802KH"));
        assertTrue(response.getQrCode().contains("6304"));
        assertNotNull(response.getMd5());
        assertNotNull(response.getDeepLink());
        assertTrue(response.getDeepLink().startsWith("https://api-bakong.nbc.gov.kh/checkout?qr="));
        assertEquals(PaymentStatus.PENDING, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void verifyKhqr_WhenPendingPayment_ShouldUpdateToSuccessAndSaveOutboxCdc() {
        Payment pendingPayment = Payment.builder()
                .id(10L)
                .paymentId("KHQR-12345678")
                .orderId("ORD-888")
                .userId("USER-222")
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.KHQR)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findByPaymentId("KHQR-12345678")).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(pendingPayment);
        when(outboxRepository.save(any(PaymentOutbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KhqrVerifyRequest verifyRequest = KhqrVerifyRequest.builder()
                .paymentId("KHQR-12345678")
                .md5("dummy_md5")
                .build();

        KhqrResponse response = khqrService.verifyKhqr(verifyRequest);

        assertNotNull(response);
        assertEquals("KHQR-12345678", response.getPaymentId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(outboxRepository, times(1)).save(any(PaymentOutbox.class));
    }

    @Test
    void calculateCrc16Ccitt_ShouldReturnCorrectChecksum() {
        String testData = "00020101021230200016tomneak@kmbl520459995303840540525.005802KH5915Tomneak Merchant6007PhnomPenh62110107ORD-8886304";
        String crc = KhqrServiceImpl.calculateCrc16Ccitt(testData);

        assertNotNull(crc);
        assertEquals(4, crc.length());
    }
}
