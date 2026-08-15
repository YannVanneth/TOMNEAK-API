package org.yannvanneth.tomneak.productservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.productservice.exception.NotFoundException;
import org.yannvanneth.tomneak.productservice.model.entity.Category;
import org.yannvanneth.tomneak.productservice.model.request.CategoryRequest;
import org.yannvanneth.tomneak.productservice.model.response.CategoryResponse;
import org.yannvanneth.tomneak.productservice.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing product category operations.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    /**
     * Retrieves all categories with pagination metadata.
     *
     * @param pageable pageable metadata
     * @return List of CategoryResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves category by unique ID.
     *
     * @param id category UUID
     * @return CategoryResponse
     * @throws NotFoundException if category is not found
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));
        return modelMapper.map(category, CategoryResponse.class);
    }

    /**
     * Creates a new product category.
     *
     * @param request category payload
     * @return CategoryResponse
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryResponse.class);
    }

    /**
     * Updates an existing product category.
     *
     * @param id category UUID
     * @param request category payload
     * @return CategoryResponse
     * @throws NotFoundException if category is not found
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return modelMapper.map(updated, CategoryResponse.class);
    }

    /**
     * Deletes a category by unique ID.
     *
     * @param id category UUID
     * @throws NotFoundException if category is not found
     */
    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
