package org.yannvanneth.tomneak.productservice.model.entity;

/**
 * ProductStatus enum representing the current state of a product.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
public enum ProductStatus {
    /** Product draft state, not yet published */
    DRAFT,
    /** Product available for purchase */
    AVAILABLE,
    /** Product currently out of stock */
    OUT_OF_STOCK,
    /** Product permanently discontinued */
    DISCONTINUED
}
