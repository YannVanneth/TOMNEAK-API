-- =============================================================
--  Product Service — Initial Schema
--  Flyway migration: V1__init_product_service.sql
--  Database: product_service_db
-- =============================================================

-- -------------------------------------------------------------
--  Extensions
-- -------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()


-- -------------------------------------------------------------
--  Table: categories
-- -------------------------------------------------------------
CREATE TABLE categories (
                            id              UUID        NOT NULL DEFAULT gen_random_uuid(),
                            name            VARCHAR(255) NOT NULL,
                            description     TEXT,
                            created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
                            updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

                            CONSTRAINT pk_categories        PRIMARY KEY (id),
                            CONSTRAINT uq_categories_name   UNIQUE (name)
);

COMMENT ON TABLE  categories             IS 'Product categories table.';
COMMENT ON COLUMN categories.id         IS 'Internal UUID primary key.';
COMMENT ON COLUMN categories.name       IS 'Unique name of category.';

CREATE INDEX idx_categories_name ON categories (name);


-- -------------------------------------------------------------
--  Table: products
-- -------------------------------------------------------------
CREATE TABLE products (
                          id              UUID            NOT NULL DEFAULT gen_random_uuid(),
                          name            VARCHAR(255)    NOT NULL,
                          description     TEXT,
                          price           NUMERIC(19, 2)  NOT NULL DEFAULT 0.00,
                          stock_quantity  INT             NOT NULL DEFAULT 0,
                          sku             VARCHAR(100)    NOT NULL,
                          category_id     UUID,
                          status          VARCHAR(50)     NOT NULL DEFAULT 'AVAILABLE',
                          is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
                          created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
                          updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

                          CONSTRAINT pk_products          PRIMARY KEY (id),
                          CONSTRAINT uq_products_sku       UNIQUE (sku),
                          CONSTRAINT fk_products_category FOREIGN KEY (category_id)
                              REFERENCES categories (id)
                              ON DELETE SET NULL
);

COMMENT ON TABLE  products                IS 'Products catalog table.';
COMMENT ON COLUMN products.sku           IS 'Stock Keeping Unit unique code.';
COMMENT ON COLUMN products.status        IS 'Product status: DRAFT, AVAILABLE, OUT_OF_STOCK, DISCONTINUED.';

CREATE INDEX idx_products_sku         ON products (sku);
CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_status      ON products (status);
CREATE INDEX idx_products_is_active   ON products (is_active);
CREATE INDEX idx_products_created_at  ON products (created_at DESC);


-- -------------------------------------------------------------
--  Table: product_images
-- -------------------------------------------------------------
CREATE TABLE product_images (
                                id              UUID        NOT NULL DEFAULT gen_random_uuid(),
                                product_id      UUID        NOT NULL,
                                image_url       TEXT        NOT NULL,
                                is_primary      BOOLEAN     NOT NULL DEFAULT FALSE,
                                created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

                                CONSTRAINT pk_product_images        PRIMARY KEY (id),
                                CONSTRAINT fk_images_product        FOREIGN KEY (product_id)
                                    REFERENCES products (id)
                                    ON DELETE CASCADE
);

COMMENT ON TABLE  product_images             IS 'Product gallery images.';

CREATE INDEX idx_images_product_id ON product_images (product_id);


-- -------------------------------------------------------------
--  Auto-update updated_at trigger
-- -------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- -------------------------------------------------------------
--  Seed data
-- -------------------------------------------------------------
INSERT INTO categories (id, name, description)
VALUES ('11111111-1111-1111-1111-111111111111', 'Electronics', 'Electronic devices and gadgets')
ON CONFLICT DO NOTHING;

INSERT INTO products (id, name, description, price, stock_quantity, sku, category_id, status)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           'Sample Product',
           'Sample description for product',
           99.99,
           100,
           'SKU-SAMPLE-001',
           '11111111-1111-1111-1111-111111111111',
           'AVAILABLE'
       ) ON CONFLICT DO NOTHING;
