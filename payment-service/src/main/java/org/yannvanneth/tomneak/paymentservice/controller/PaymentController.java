package org.yannvanneth.tomneak.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yannvanneth.tomneak.paymentservice.model.request.PaymentRequest;
import org.yannvanneth.tomneak.paymentservice.model.response.ApiResponse;
import org.yannvanneth.tomneak.paymentservice.model.response.ApiResponseFactory;
import org.yannvanneth.tomneak.paymentservice.model.response.PaymentResponse;
import org.yannvanneth.tomneak.paymentservice.service.PaymentService;

import java.util.List;

/**
 * PaymentController exposes REST API endpoints for initiating and querying payment transactions.
 *
 * @author Yann Yanneth
 * @since 2026-08-09
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for processing and managing payment transactions")
public class PaymentController {

    private final PaymentService paymentService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * Endpoint to initiate a payment transaction.
     *
     * @param request PaymentRequest payload
     * @return ApiResponse containing PaymentResponse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Process a new payment", description = "Processes a payment transaction and publishes SAGA event via CDC Outbox")
    public ApiResponse<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return apiResponseFactory.created(response, "Payment processed successfully");
    }

    /**
     * Endpoint to retrieve payment details by paymentId.
     *
     * @param paymentId payment identifier
     * @return ApiResponse containing PaymentResponse
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment transaction details by unique payment ID")
    public ApiResponse<PaymentResponse> getPaymentById(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return apiResponseFactory.success(response, "Payment retrieved successfully");
    }

    /**
     * Endpoint to retrieve payments by orderId.
     *
     * @param orderId order identifier
     * @return ApiResponse containing List of PaymentResponse
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payments by order ID", description = "Retrieves payments associated with an order ID")
    public ApiResponse<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable String orderId) {
        List<PaymentResponse> response = paymentService.getPaymentsByOrderId(orderId);
        return apiResponseFactory.success(response, "Payments for order retrieved successfully");
    }

    /**
     * Endpoint to cancel or refund a payment as a SAGA compensating transaction.
     *
     * @param paymentId payment identifier
     * @param reason cancellation reason query param
     * @return ApiResponse containing updated PaymentResponse
     */
    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel / Refund payment", description = "Cancels or refunds a payment as part of a SAGA compensating workflow")
    public ApiResponse<PaymentResponse> cancelPayment(@PathVariable String paymentId,
                                                      @RequestParam(defaultValue = "Customer requested cancellation") String reason) {
        PaymentResponse response = paymentService.cancelPayment(paymentId, reason);
        return apiResponseFactory.success(response, "Payment cancelled successfully");
    }
}
