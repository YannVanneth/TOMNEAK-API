package org.yannvanneth.tomneak.paymentservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * KhqrVerifyRequest DTO carrying parameters to verify a KHQR transaction status.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhqrVerifyRequest {

    private String paymentId;
    private String md5;
}
