package org.yannvanneth.tomneak.paymentservice.exception;

/**
 * Custom Exception thrown when payment processing fails.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
