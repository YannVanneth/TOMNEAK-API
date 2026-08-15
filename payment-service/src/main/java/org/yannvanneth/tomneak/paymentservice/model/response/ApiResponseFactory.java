package org.yannvanneth.tomneak.paymentservice.model.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * ApiResponseFactory class for building standardized ApiResponse objects.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Component
@RequiredArgsConstructor
public class ApiResponseFactory {

    private final HttpServletRequest request;

    /**
     * Creates a success (200 OK) ApiResponse object.
     *
     * @param <T> payload type
     * @param payload response payload
     * @param message response description message
     * @return ApiResponse<T>
     */
    public <T> ApiResponse<T> success(T payload, String message) {
        return success(payload, message, HttpStatus.OK);
    }

    /**
     * Creates a created (201 CREATED) ApiResponse object.
     *
     * @param <T> payload type
     * @param payload response payload
     * @param message response description message
     * @return ApiResponse<T>
     */
    public <T> ApiResponse<T> created(T payload, String message) {
        return success(payload, message, HttpStatus.CREATED);
    }

    /**
     * Creates an ApiResponse object with a custom HttpStatus.
     *
     * @param <T> payload type
     * @param payload response payload
     * @param message response description message
     * @param status HTTP Status
     * @return ApiResponse<T>
     */
    public <T> ApiResponse<T> success(T payload, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .payload(payload)
                .message(message)
                .status(status)
                .timestamp(Instant.now())
                .path(request != null ? request.getRequestURI() : "/api/v1/payments")
                .build();
    }
}
