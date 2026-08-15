package org.yannvanneth.tomneak.productservice.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yannvanneth.tomneak.productservice.model.entity.ProductStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for creating or updating a product.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    /** Name of the product */
    @NotBlank(message = "Product name is required")
    private String name;

    /** Product description */
    private String description;

    /** Product price */
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private BigDecimal price;

    /** Quantity of stock */
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    /** Unique Stock Keeping Unit code */
    @NotBlank(message = "SKU is required")
    private String sku;

    /** Associated category UUID */
    private UUID categoryId;

    /** Product status */
    private ProductStatus status;

    /** List of image URLs associated with product */
    private List<String> imageUrls;
}
