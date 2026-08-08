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
import org.yannvanneth.tomneak.productservice.model.entity.Product;
import org.yannvanneth.tomneak.productservice.model.entity.ProductStatus;
import org.yannvanneth.tomneak.productservice.model.request.ProductRequest;
import org.yannvanneth.tomneak.productservice.model.response.ProductResponse;
import org.yannvanneth.tomneak.productservice.repository.CategoryRepository;
import org.yannvanneth.tomneak.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapperConfig().modelMapper();

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("1200.00"))
                .stockQuantity(10)
                .sku("SKU-LAP-001")
                .status(ProductStatus.AVAILABLE)
                .isActive(true)
                .images(new ArrayList<>())
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.searchProducts(null, pageable)).thenReturn(new PageImpl<>(List.of(sampleProduct)));

        List<ProductResponse> responses = productService.getAllProducts(null, pageable);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
    }

    @Test
    void getProductById_WhenFound_ShouldReturnProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct));

        ProductResponse response = productService.getProductById(productId);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals("SKU-LAP-001", response.getSku());
    }

    @Test
    void getProductById_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("1200.00"))
                .stockQuantity(10)
                .sku("SKU-LAP-001")
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productRepository.existsBySku("SKU-LAP-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
