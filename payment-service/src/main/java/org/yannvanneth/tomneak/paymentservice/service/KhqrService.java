package org.yannvanneth.tomneak.paymentservice.service;

import org.yannvanneth.tomneak.paymentservice.model.request.KhqrGenerateRequest;
import org.yannvanneth.tomneak.paymentservice.model.request.KhqrVerifyRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.KhqrResponse;

/**
 * KhqrService interface for NBC Bakong KHQR QR code generation, CRC16 computation, and payment verification.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public interface KhqrService {

    /**
     * Generates a Bakong KHQR code, MD5 hash, and deep link for a payment transaction.
     *
     * @param request KhqrGenerateRequest DTO
     * @return KhqrResponse DTO
     */
    KhqrResponse generateKhqr(KhqrGenerateRequest request);

    /**
     * Verifies a KHQR payment transaction and fires SAGA PAYMENT_COMPLETED event via CDC Outbox.
     *
     * @param request KhqrVerifyRequest DTO
     * @return KhqrResponse DTO
     */
    KhqrResponse verifyKhqr(KhqrVerifyRequest request);
}
