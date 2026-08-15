package org.yannvanneth.tomneak.paymentservice.service;

import org.yannvanneth.tomneak.paymentservice.model.request.PaymentRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.PaymentResponse;

import java.util.List;

/**
 * Service interface for managing Payment business operations and SAGA transaction processing.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public interface PaymentService {

    /**
     * Processes a payment transaction atomically creating the Payment entity and CDC Outbox event log.
     *
     * @param request PaymentRequest DTO
     * @return PaymentResponse DTO
     */
    PaymentResponse processPayment(PaymentRequest request);

    /**
     * Retrieves a payment by its unique paymentId.
     *
     * @param paymentId payment identifier
     * @return PaymentResponse DTO
     */
    PaymentResponse getPaymentById(String paymentId);

    /**
     * Retrieves all payments associated with a specific orderId.
     *
     * @param orderId order identifier
     * @return List of PaymentResponse DTOs
     */
    List<PaymentResponse> getPaymentsByOrderId(String orderId);

    /**
     * Cancels / Refunds a payment as part of a SAGA compensating transaction.
     *
     * @param paymentId payment identifier
     * @param reason cancellation or refund reason
     * @return PaymentResponse DTO
     */
    PaymentResponse cancelPayment(String paymentId, String reason);
}
