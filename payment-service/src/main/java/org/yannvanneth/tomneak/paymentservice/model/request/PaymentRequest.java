package org.yannvanneth.tomneak.paymentservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentMethod;

import java.math.BigDecimal;

/**
 * PaymentRequest DTO carrying parameters to initiate a payment transaction.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private String orderId;
    private String userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}
