package org.yannvanneth.tomneak.paymentservice.cdc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.yannvanneth.tomneak.paymentservice.model.entity.OutboxStatus;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentOutbox;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOutboxPublisherTest {

    @Mock
    private PaymentOutboxRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private PaymentOutboxPublisher outboxPublisher;

    private PaymentOutbox pendingOutbox;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(outboxPublisher, "topicName", "payment.events");

        pendingOutbox = PaymentOutbox.builder()
                .id(1L)
                .aggregateType("PAYMENT")
                .aggregateId("PAY-12345678")
                .eventType("PAYMENT_COMPLETED")
                .payload("{\"eventType\":\"PAYMENT_COMPLETED\",\"paymentId\":\"PAY-12345678\"}")
                .status(OutboxStatus.PENDING)
                .build();
    }

    @Test
    void publishPendingOutboxEvents_WhenPendingRecordsExist_ShouldPublishToKafkaAndMarkProcessed() {
        when(outboxRepository.findByStatus(OutboxStatus.PENDING)).thenReturn(List.of(pendingOutbox));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        outboxPublisher.publishPendingOutboxEvents();

        verify(kafkaTemplate, times(1)).send(eq("payment.events"), eq("PAY-12345678"), eq(pendingOutbox.getPayload()));
        verify(outboxRepository, times(1)).save(argThat(outbox -> outbox.getStatus() == OutboxStatus.PROCESSED));
    }

    @Test
    void publishPendingOutboxEvents_WhenNoPendingRecords_ShouldDoNothing() {
        when(outboxRepository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.emptyList());

        outboxPublisher.publishPendingOutboxEvents();

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        verify(outboxRepository, never()).save(any());
    }
}
