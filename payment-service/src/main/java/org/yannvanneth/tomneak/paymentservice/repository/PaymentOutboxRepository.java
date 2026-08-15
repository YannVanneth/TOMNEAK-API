package org.yannvanneth.tomneak.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yannvanneth.tomneak.paymentservice.model.entity.OutboxStatus;
import org.yannvanneth.tomneak.paymentservice.model.entity.PaymentOutbox;

import java.util.List;

/**
 * Repository interface for managing PaymentOutbox entities in the CDC pattern.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Repository
public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, Long> {

    /**
     * Finds outbox records by outbox processing status.
     *
     * @param status outbox status
     * @return List of PaymentOutbox entities
     */
    List<PaymentOutbox> findByStatus(OutboxStatus status);
}
