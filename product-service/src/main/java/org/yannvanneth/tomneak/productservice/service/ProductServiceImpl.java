package org.yannvanneth.tomneak.productservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.productservice.exception.NotFoundException;
import org.yannvanneth.tomneak.productservice.model.entity.Category;
import org.yannvanneth.tomneak.productservice.model.entity.Product;
import org.yannvanneth.tomneak.productservice.model.entity.ProductImage;
import org.yannvanneth.tomneak.productservice.model.entity.ProductStatus;
import org.yannvanneth.tomneak.productservice.model.request.ProductRequest;
import org.yannvanneth.tomneak.productservice.model.response.CategoryResponse;
import org.yannvanneth.tomneak.productservice.model.response.ProductResponse;
import org.yannvanneth.tomneak.productservice.repository.CategoryRepository;
import org.yannvanneth.tomneak.productservice.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing product operations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    /**
     * Retrieves products with pagination and search filter.
     *
     * @param search optional search keyword
     * @param pageable pageable metadata
     * @return List of ProductResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(String search, Pageable pageable) {
        return productRepository.searchProducts(search, pageable).getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves product by unique ID.
     *
     * @param id product UUID
     * @return ProductResponse
     * @throws NotFoundException if product is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return mapToResponse(product);
    }

    /**
     * Creates a new product.
     *
     * @param request product payload
     * @return ProductResponse
     */
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product SKU already exists: " + request.getSku());
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with ID: " + request.getCategoryId()));
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .category(category)
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.AVAILABLE)
                .isActive(true)
                .images(new ArrayList<>())
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            boolean isFirst = true;
            for (String url : request.getImageUrls()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(url)
                        .isPrimary(isFirst)
                        .build();
                product.getImages().add(image);
                isFirst = false;
            }
        }

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    /**
     * Updates an existing product.
     *
     * @param id product UUID
     * @param request product payload
     * @return ProductResponse
     * @throws NotFoundException if product is not found
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product SKU already exists: " + request.getSku());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        if (request.getImageUrls() != null) {
            product.getImages().clear();
            boolean isFirst = true;
            for (String url : request.getImageUrls()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(url)
                        .isPrimary(isFirst)
                        .build();
                product.getImages().add(image);
                isFirst = false;
            }
        }

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    /**
     * Soft deletes a product by unique ID.
     *
     * @param id product UUID
     * @throws NotFoundException if product is not found
     */
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * Maps Product entity to ProductResponse DTO.
     *
     * @param product Product entity
     * @return ProductResponse DTO
     */
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        if (product.getCategory() != null) {
            response.setCategory(modelMapper.map(product.getCategory(), CategoryResponse.class));
        }
        if (product.getImages() != null) {
            response.setImageUrls(product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        }
        return response;
    }
}
