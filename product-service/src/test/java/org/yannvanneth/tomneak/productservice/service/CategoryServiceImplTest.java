package org.yannvanneth.tomneak.productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.yannvanneth.tomneak.productservice.config.ModelMapperConfig;
import org.yannvanneth.tomneak.productservice.exception.NotFoundException;
import org.yannvanneth.tomneak.productservice.model.entity.Category;
import org.yannvanneth.tomneak.productservice.model.request.CategoryRequest;
import org.yannvanneth.tomneak.productservice.model.response.CategoryResponse;
import org.yannvanneth.tomneak.productservice.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapperConfig().modelMapper();

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        sampleCategory = Category.builder()
                .id(categoryId)
                .name("Electronics")
                .description("Gadgets & Devices")
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sampleCategory)));

        List<CategoryResponse> responses = categoryService.getAllCategories(pageable);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Electronics", responses.get(0).getName());
    }

    @Test
    void getCategoryById_WhenFound_ShouldReturnCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        assertNotNull(response);
        assertEquals("Electronics", response.getName());
    }

    @Test
    void getCategoryById_WhenNotFound_ShouldThrowException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(categoryId));
    }

    @Test
    void createCategory_ShouldSaveAndReturnCategory() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .description("Gadgets & Devices")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Electronics", response.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenFound_ShouldDelete() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
