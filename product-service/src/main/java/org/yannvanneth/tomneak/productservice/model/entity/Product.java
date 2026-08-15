package org.yannvanneth.tomneak.productservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a product in the catalog.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    /** Internal UUID primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Product title/name */
    @Column(nullable = false)
    private String name;

    /** Detailed product description */
    private String description;

    /** Unit price of product */
    @Column(nullable = false)
    private BigDecimal price;

    /** Quantity of stock available */
    @Column(nullable = false)
    private Integer stockQuantity;

    /** Unique Stock Keeping Unit code */
    @Column(unique = true, nullable = false)
    private String sku;

    /** Category to which this product belongs */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /** Current availability status of the product */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    /** Soft-delete flag indicating active state */
    @Builder.Default
    private Boolean isActive = true;

    /** Associated product gallery images */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<ProductImage> images = new ArrayList<>();

    /** Creation timestamp */
    @Column(updatable = false)
    private Instant createdAt;

    /** Last update timestamp */
    @Column(insertable = false)
    private Instant updatedAt;
}
