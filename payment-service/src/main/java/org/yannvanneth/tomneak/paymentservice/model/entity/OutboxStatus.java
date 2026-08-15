package org.yannvanneth.tomneak.paymentservice.model.entity;

/**
 * OutboxStatus enumeration representing state of Outbox records in Change Data Capture (CDC) pattern.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}
