package org.yannvanneth.tomneak.paymentservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * KhqrResponse DTO carrying generated Bakong KHQR QR string, MD5 hash, and deep link information.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhqrResponse {

    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency; // "USD" or "KHR"
    private String qrCode;   // Raw EMVCo KHQR String
    private String md5;      // Transaction MD5 Hash
    private String deepLink; // Bakong App checkout deep link URL
    private String merchantName;
    private PaymentStatus status;
    private ZonedDateTime createdAt;
}
