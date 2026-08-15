package org.yannvanneth.tomneak.paymentservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentStatus;

import java.math.BigDecimal;

/**
 * PaymentEvent DTO representing SAGA workflow event payloads published via CDC Outbox to Kafka.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private String eventType;
    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String failureReason;
}
