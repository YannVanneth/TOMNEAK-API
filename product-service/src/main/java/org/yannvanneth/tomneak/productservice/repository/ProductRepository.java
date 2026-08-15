package org.yannvanneth.tomneak.productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yannvanneth.tomneak.productservice.model.entity.Product;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Product entity database operations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Finds product by SKU code.
     *
     * @param sku product SKU code
     * @return Optional containing Product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Checks if product exists by SKU code.
     *
     * @param sku product SKU code
     * @return true if product SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Searches active products matching search query by name or SKU.
     *
     * @param search optional search keyword
     * @param pageable pagination metadata
     * @return Page of active products
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);
}
