package org.yannvanneth.tomneak.productservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating or updating a product category.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    /** Name of the category */
    @NotBlank(message = "Category name is required")
    private String name;

    /** Description of the category */
    private String description;
}
