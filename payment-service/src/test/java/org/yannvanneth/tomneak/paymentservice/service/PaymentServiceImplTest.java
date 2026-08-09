package org.yannvanneth.tomneak.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.yannvanneth.tomneak.paymentservice.exception.NotFoundException;
import org.yannvanneth.tomneak.paymentservice.model.entity.*;
import org.yannvanneth.tomneak.paymentservice.model.request.PaymentRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.PaymentResponse;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentOutboxRepository outboxRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment mockPayment;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        mockPayment = Payment.builder()
                .id(1L)
                .paymentId("PAY-12345678")
                .orderId("ORD-999")
                .userId("USER-111")
                .amount(new BigDecimal("150.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentRequest = PaymentRequest.builder()
                .orderId("ORD-999")
                .userId("USER-111")
                .amount(new BigDecimal("150.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();
    }

    @Test
    void processPayment_WhenValidRequest_ShouldCreatePaymentAndOutboxRecord() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);
        when(outboxRepository.save(any(PaymentOutbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals("PAY-12345678", response.getPaymentId());
        assertEquals("ORD-999", response.getOrderId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(outboxRepository, times(1)).save(any(PaymentOutbox.class));
    }

    @Test
    void getPaymentById_WhenPaymentExists_ShouldReturnPaymentResponse() {
        when(paymentRepository.findByPaymentId("PAY-12345678")).thenReturn(Optional.of(mockPayment));

        PaymentResponse response = paymentService.getPaymentById("PAY-12345678");

        assertNotNull(response);
        assertEquals("PAY-12345678", response.getPaymentId());
        verify(paymentRepository, times(1)).findByPaymentId("PAY-12345678");
    }

    @Test
    void getPaymentById_WhenPaymentDoesNotExist_ShouldThrowNotFoundException() {
        when(paymentRepository.findByPaymentId("PAY-99999999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.getPaymentById("PAY-99999999"));
    }

    @Test
    void getPaymentsByOrderId_ShouldReturnPaymentList() {
        when(paymentRepository.findByOrderId("ORD-999")).thenReturn(List.of(mockPayment));

        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId("ORD-999");

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("ORD-999", responses.get(0).getOrderId());
    }

    @Test
    void cancelPayment_WhenPaymentExists_ShouldUpdateStatusToRefundedAndCreateOutboxRecord() {
        when(paymentRepository.findByPaymentId("PAY-12345678")).thenReturn(Optional.of(mockPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);
        when(outboxRepository.save(any(PaymentOutbox.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.cancelPayment("PAY-12345678", "User requested refund");

        assertNotNull(response);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(outboxRepository, times(1)).save(any(PaymentOutbox.class));
    }
}
