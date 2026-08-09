package org.yannvanneth.tomneak.paymentservice.model.entity;

/**
 * PaymentStatus enumeration representing possible states of a payment transaction in the SAGA workflow.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}
