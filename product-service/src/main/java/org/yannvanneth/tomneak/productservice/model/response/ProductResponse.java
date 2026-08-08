package org.yannvanneth.tomneak.productservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.productservice.model.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for product response representations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    /** Unique product identifier */
    private UUID id;

    /** Product name */
    private String name;

    /** Product description */
    private String description;

    /** Unit price */
    private BigDecimal price;

    /** Current stock quantity */
    private Integer stockQuantity;

    /** Unique SKU code */
    private String sku;

    /** Associated Category details */
    private CategoryResponse category;

    /** Current product status */
    private ProductStatus status;

    /** Active status flag */
    private Boolean isActive;

    /** List of gallery image URLs */
    private List<String> imageUrls;

    /** Creation timestamp */
    private Instant createdAt;

    /** Last update timestamp */
    private Instant updatedAt;
}
