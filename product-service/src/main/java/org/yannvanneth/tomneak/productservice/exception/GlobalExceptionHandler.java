package org.yannvanneth.tomneak.productservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponse;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponseFactory;

/**
 * GlobalExceptionHandler handles application-wide exceptions and formats error responses.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseFactory responseFactory;

    /**
     * Handles NotFoundException and returns a NOT_FOUND (404) response.
     *
     * @param ex the NotFoundException
     * @return ResponseEntity with ApiResponse payload
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        ApiResponse<Void> response = responseFactory.success(null, ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException and returns a BAD_REQUEST (400) response.
     *
     * @param ex the IllegalArgumentException
     * @return ResponseEntity with ApiResponse payload
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = responseFactory.success(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general unexpected exceptions and returns an INTERNAL_SERVER_ERROR (500) response.
     *
     * @param ex the Exception
     * @return ResponseEntity with ApiResponse payload
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse<Void> response = responseFactory.success(null, "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
