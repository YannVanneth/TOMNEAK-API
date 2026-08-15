package org.yannvanneth.tomneak.productservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for category response representations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    /** Unique identifier of category */
    private UUID id;

    /** Category name */
    private String name;

    /** Category description */
    private String description;

    /** Timestamp when category was created */
    private Instant createdAt;

    /** Timestamp when category was last updated */
    private Instant updatedAt;
}
