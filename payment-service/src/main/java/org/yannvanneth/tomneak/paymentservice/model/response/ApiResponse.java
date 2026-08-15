package org.yannvanneth.tomneak.paymentservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Standardized API response wrapper for Payment Service.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 * @param <T> Payload type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private T payload;
    private String message;
    private HttpStatus status;
    private Instant timestamp;
    private String path;
}
