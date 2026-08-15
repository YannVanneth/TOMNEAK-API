package org.yannvanneth.tomneak.productservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Standardized API response wrapper structure.
 *
 * @param <T> payload type
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ApiResponse<T> {

    /** Response payload */
    private T payload;

    /** Response message */
    private String message;

    /** HTTP Status */
    private HttpStatus status;

    /** Timestamp of response */
    private Instant timestamp;

    /** Request URI path */
    private String path;
}
