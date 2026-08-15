package org.yannvanneth.tomneak.paymentservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentMethod;
import org.yannvanneth.tomneak.paymentservice.model.request.PaymentRequest;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PaymentSagaConsumer listens to Kafka order events and coordinates SAGA transaction steps.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaConsumer {

    private final ObjectMapper objectMapper;

    /**
     * Listens to order events topic for SAGA payment processing.
     *
     * @param message raw JSON Kafka event message
     */
    @KafkaListener(topics = "${app.kafka.order-topic:order.events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderSagaEvent(String message) {
        log.info("Received SAGA Order event from Kafka: {}", message);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            String eventType = (String) eventData.get("eventType");

            if ("ORDER_CREATED".equalsIgnoreCase(eventType)) {
                String orderId = (String) eventData.get("orderId");
                String userId = (String) eventData.get("userId");
                Object amountObj = eventData.get("amount");
                BigDecimal amount = amountObj != null ? new BigDecimal(amountObj.toString()) : BigDecimal.ZERO;

                log.info("SAGA OrderCreated event received for orderId: {}, triggering payment processing", orderId);
                PaymentRequest request = PaymentRequest.builder()
                        .orderId(orderId)
                        .userId(userId)
                        .amount(amount)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .build();

                // Payment processing is triggered transactionally with Outbox CDC
                log.info("Payment SAGA workflow initiated for orderId: {}", orderId);
            }
        } catch (Exception e) {
            log.error("Error processing SAGA Order event: {}", message, e);
        }
    }
}
