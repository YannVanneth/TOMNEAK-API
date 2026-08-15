package org.yannvanneth.tomneak.paymentservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yannvanneth.tomneak.paymentservice.model.response.ApiResponse;

import java.time.Instant;

/**
 * GlobalExceptionHandler handles uncaught exceptions and converts them into standardized ApiResponse payloads.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ApiResponse<Object> response = ApiResponse.builder()
                .payload(null)
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handlePaymentFailedException(PaymentFailedException ex, HttpServletRequest request) {
        ApiResponse<Object> response = ApiResponse.builder()
                .payload(null)
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex, HttpServletRequest request) {
        ApiResponse<Object> response = ApiResponse.builder()
                .payload(null)
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
