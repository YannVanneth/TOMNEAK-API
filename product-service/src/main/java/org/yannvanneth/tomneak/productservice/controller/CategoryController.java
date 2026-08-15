package org.yannvanneth.tomneak.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yannvanneth.tomneak.productservice.model.request.CategoryRequest;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponse;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponseFactory;
import org.yannvanneth.tomneak.productservice.model.response.CategoryResponse;
import org.yannvanneth.tomneak.productservice.service.CategoryService;

import java.util.List;
import java.util.UUID;

/**
 * @author Yann Vanneth
 * @since 2026-08-09
 * {@code @description} CategoryController class for managing product categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ApiResponseFactory responseFactory;

    /**
     * Retrieves product categories
     * @param page use for pagination which page to retrieve and has default value 1
     * @param size use for pagination which sizes to retrieve per page and has default value 10
     * @return ResponseEntity<List<CategoryResponse>>
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<CategoryResponse> categories = categoryService.getAllCategories(Pageable.ofSize(size).withPage(page - 1));
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves category by id
     * @param id category id
     * @return ResponseEntity<CategoryResponse>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("id") UUID id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Create product category
     * @param request category payload
     * @return ResponseEntity<ApiResponse<CategoryResponse>>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> response = responseFactory.created(category, "Create category successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Update product category
     * @param id category id
     * @param request category payload
     * @return ResponseEntity<ApiResponse<CategoryResponse>>
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable("id") UUID id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponse> response = responseFactory.success(category, "Update category successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete product category
     * @param id category id
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(responseFactory.success(null, "Delete category successfully"));
    }
}
