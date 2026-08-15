package org.yannvanneth.tomneak.productservice.exception;

/**
 * Custom runtime exception thrown when a requested resource is not found.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
