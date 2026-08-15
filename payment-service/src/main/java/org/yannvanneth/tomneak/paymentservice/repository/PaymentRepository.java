package org.yannvanneth.tomneak.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yannvanneth.tomneak.paymentservice.model.entity.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Payment entities.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds a payment by its unique business paymentId string.
     *
     * @param paymentId payment identifier
     * @return Optional containing Payment if found
     */
    Optional<Payment> findByPaymentId(String paymentId);

    /**
     * Finds payments associated with a specific orderId.
     *
     * @param orderId order identifier
     * @return List of Payment entities
     */
    List<Payment> findByOrderId(String orderId);

    /**
     * Finds payments associated with a specific userId.
     *
     * @param userId user identifier
     * @return List of Payment entities
     */
    List<Payment> findByUserId(String userId);
}
