package org.yannvanneth.tomneak.paymentservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentMethod;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * PaymentResponse DTO carrying payment transaction details in API responses.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String failureReason;
    private ZonedDateTime createdAt;
}
