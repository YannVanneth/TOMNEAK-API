package org.yannvanneth.tomneak.paymentservice.cdc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.paymentservice.model.entity.OutboxStatus;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentOutbox;
import org.yannvanneth.tomneak.paymentservice.repository.PaymentOutboxRepository;

import java.util.List;

/**
 * PaymentOutboxPublisher implements the Transactional Outbox / Change Data Capture (CDC) pattern.
 * Periodically polls the payment_outbox database table for PENDING events, dispatches them safely to Apache Kafka,
 * and updates outbox record status to PROCESSED.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxPublisher {

    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic:payment.events}")
    private String topicName;

    /**
     * Scheduled worker executing every 3 seconds to poll and publish PENDING outbox CDC events.
     */
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingOutboxEvents() {
        List<PaymentOutbox> pendingEvents = outboxRepository.findByStatus(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("CDC Outbox Publisher found {} pending events to dispatch to Kafka topic '{}'", pendingEvents.size(), topicName);

        for (PaymentOutbox outbox : pendingEvents) {
            try {
                // Publish CDC payload to Kafka topic
                kafkaTemplate.send(topicName, outbox.getAggregateId(), outbox.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Successfully dispatched outbox CDC event id {} for aggregate {} to Kafka",
                                        outbox.getId(), outbox.getAggregateId());
                            } else {
                                log.error("Failed to dispatch outbox CDC event id {} to Kafka", outbox.getId(), ex);
                            }
                        });

                // Mark outbox status as PROCESSED after dispatch attempt
                outbox.setStatus(OutboxStatus.PROCESSED);
                outboxRepository.save(outbox);
            } catch (Exception e) {
                log.error("Exception during CDC outbox event publication for id: {}", outbox.getId(), e);
                outbox.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(outbox);
            }
        }
    }
}
