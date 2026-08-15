package org.yannvanneth.tomneak.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yannvanneth.tomneak.productservice.model.entity.Category;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Category entity database operations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Finds category by name ignoring case.
     *
     * @param name category name
     * @return Optional containing Category if found
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Checks if category exists by name ignoring case.
     *
     * @param name category name
     * @return true if category exists
     */
    boolean existsByNameIgnoreCase(String name);
}
