package org.yannvanneth.tomneak.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yannvanneth.tomneak.productservice.model.request.ProductRequest;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponse;
import org.yannvanneth.tomneak.productservice.model.response.ApiResponseFactory;
import org.yannvanneth.tomneak.productservice.model.response.ProductResponse;
import org.yannvanneth.tomneak.productservice.service.ProductService;

import java.util.List;
import java.util.UUID;

/**
 * @author Yann Vanneth
 * @since 2026-08-09
 * {@code @description} ProductController class for managing products.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ApiResponseFactory responseFactory;

    /**
     * Retrieves products with pagination and optional search filter
     * @param page use for pagination which page to retrieve and has default value 1
     * @param size use for pagination which sizes to retrieve per page and has default value 10
     * @param search search filter by name or sku
     * @return ResponseEntity<List<ProductResponse>>
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) String search) {
        List<ProductResponse> products = productService.getAllProducts(search, Pageable.ofSize(size).withPage(page - 1));
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves product by id
     * @param id product id
     * @return ResponseEntity<ProductResponse>
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Create product
     * @param request product payload
     * @return ResponseEntity<ApiResponse<ProductResponse>>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        ApiResponse<ProductResponse> response = responseFactory.created(product, "Create product successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Update product
     * @param id product id
     * @param request product payload
     * @return ResponseEntity<ApiResponse<ProductResponse>>
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable("id") UUID id, @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        ApiResponse<ProductResponse> response = responseFactory.success(product, "Update product successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete product
     * @param id product id
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(responseFactory.success(null, "Delete product successfully"));
    }
}
