package org.yannvanneth.tomneak.paymentservice.exception;

/**
 * Custom Exception thrown when a payment entity is not found.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
