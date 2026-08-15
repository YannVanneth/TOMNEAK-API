package org.yannvanneth.tomneak.paymentservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KhqrGenerateRequest DTO carrying request parameters to generate a Bakong KHQR code.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhqrGenerateRequest {

    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency; // "USD" or "KHR"
    private String merchantName;
    private String bakongAccountId;
}
