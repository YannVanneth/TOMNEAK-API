package org.yannvanneth.tomneak.productservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a product category in the system.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {

    /** Internal UUID primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Unique category name */
    @Column(unique = true, nullable = false)
    private String name;

    /** Detailed category description */
    private String description;

    /** Creation timestamp */
    @Column(updatable = false)
    private Instant createdAt;

    /** Last update timestamp */
    @Column(insertable = false)
    private Instant updatedAt;
}
