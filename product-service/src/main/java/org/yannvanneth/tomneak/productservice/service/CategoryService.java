package org.yannvanneth.tomneak.productservice.service;

import org.springframework.data.domain.Pageable;
import org.yannvanneth.tomneak.productservice.exception.NotFoundException;
import org.yannvanneth.tomneak.productservice.model.request.CategoryRequest;
import org.yannvanneth.tomneak.productservice.model.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing category domain logic.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public interface CategoryService {

    /**
     * Retrieves all categories with pagination metadata.
     *
     * @param pageable pageable metadata
     * @return List of CategoryResponse
     */
    List<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Retrieves category by unique ID.
     *
     * @param id category UUID
     * @return CategoryResponse
     * @throws NotFoundException if category is not found
     */
    CategoryResponse getCategoryById(UUID id);

    /**
     * Creates a new product category.
     *
     * @param request category payload
     * @return CategoryResponse
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Updates an existing product category.
     *
     * @param id category UUID
     * @param request category payload
     * @return CategoryResponse
     * @throws NotFoundException if category is not found
     */
    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    /**
     * Deletes a category by unique ID.
     *
     * @param id category UUID
     * @throws NotFoundException if category is not found
     */
    void deleteCategory(UUID id);
}
