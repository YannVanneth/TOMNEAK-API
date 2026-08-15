package org.yannvanneth.tomneak.productservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing an image in a product's gallery.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product_images")
public class ProductImage {

    /** Internal UUID primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Product associated with this image */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Direct URL of the image resource */
    @Column(nullable = false)
    private String imageUrl;

    /** Flag specifying if this is the primary thumbnail image */
    @Builder.Default
    private Boolean isPrimary = false;

    /** Creation timestamp */
    @Column(updatable = false)
    private Instant createdAt;
}
