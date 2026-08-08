package org.yannvanneth.tomneak.productservice.service;

import org.springframework.data.domain.Pageable;
import org.yannvanneth.tomneak.productservice.exception.NotFoundException;
import org.yannvanneth.tomneak.productservice.model.request.ProductRequest;
import org.yannvanneth.tomneak.productservice.model.response.ProductResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing product domain logic.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public interface ProductService {

    /**
     * Retrieves products with pagination and search filter.
     *
     * @param search optional search keyword
     * @param pageable pageable metadata
     * @return List of ProductResponse
     */
    List<ProductResponse> getAllProducts(String search, Pageable pageable);

    /**
     * Retrieves product by unique ID.
     *
     * @param id product UUID
     * @return ProductResponse
     * @throws NotFoundException if product is not found
     */
    ProductResponse getProductById(UUID id);

    /**
     * Creates a new product.
     *
     * @param request product payload
     * @return ProductResponse
     */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Updates an existing product.
     *
     * @param id product UUID
     * @param request product payload
     * @return ProductResponse
     * @throws NotFoundException if product is not found
     */
    ProductResponse updateProduct(UUID id, ProductRequest request);

    /**
     * Soft deletes a product by unique ID.
     *
     * @param id product UUID
     * @throws NotFoundException if product is not found
     */
    void deleteProduct(UUID id);
}
