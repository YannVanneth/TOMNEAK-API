-- =============================================================
--  User Service — Initial Schema
--  Flyway migration: V1__init_user_service.sql
--  Database: user_service_db
--  Auth: Keycloak owns credentials/roles; this DB owns profile data only
-- =============================================================

-- -------------------------------------------------------------
--  Extensions
-- -------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "citext";     -- case-insensitive text for email lookups


-- -------------------------------------------------------------
--  Table: users
--  One row per registered user. keycloak_id is the "sub" claim
--  from the JWT and is the primary link to Keycloak's identity.
-- -------------------------------------------------------------
CREATE TABLE users (
                       id              UUID        NOT NULL DEFAULT gen_random_uuid(),
                       keycloak_id     UUID        NOT NULL,
                       full_name       VARCHAR(255) NOT NULL,
                       email           CITEXT      NOT NULL,
                       role            VARCHAR(50),
                       image_url       TEXT,
                       is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
                       created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

                       CONSTRAINT pk_users            PRIMARY KEY (id),
                       CONSTRAINT uq_users_keycloak   UNIQUE (keycloak_id),
                       CONSTRAINT uq_users_email      UNIQUE (email)
);

COMMENT ON TABLE  users              IS 'Business profile for each registered user. Credentials and roles are managed by Keycloak.';
COMMENT ON COLUMN users.id          IS 'Internal UUID primary key.';
COMMENT ON COLUMN users.keycloak_id IS 'The "sub" claim from the Keycloak JWT. Used to link inbound requests to a user profile.';
COMMENT ON COLUMN users.is_active   IS 'Soft-delete flag. Set to false instead of deleting the row.';

CREATE INDEX idx_users_keycloak_id ON users (keycloak_id);
CREATE INDEX idx_users_email       ON users (email);
CREATE INDEX idx_users_is_active   ON users (is_active);
CREATE INDEX idx_users_created_at  ON users (created_at DESC);


-- -------------------------------------------------------------
--  Table: addresses
--  A user can have multiple saved addresses. One is flagged
--  as default. The order-service snapshots address data at
--  order time so changes here do not affect past orders.
-- -------------------------------------------------------------
CREATE TABLE addresses (
                           id              UUID        NOT NULL DEFAULT gen_random_uuid(),
                           user_id         UUID        NOT NULL,
                           label           VARCHAR(50) NOT NULL,    -- e.g. "Home", "Office", "Warehouse"
                           recipient_name  VARCHAR(255) NOT NULL,
                           phone_number    VARCHAR(50) NOT NULL,
                           street          VARCHAR(255) NOT NULL,
                           city            VARCHAR(255) NOT NULL,
                           state           VARCHAR(255) NOT NULL,
                           postal_code     VARCHAR(255) NOT NULL,
                           country         VARCHAR(255) NOT NULL DEFAULT 'KH',
                           is_default      BOOLEAN     NOT NULL DEFAULT FALSE,
                           created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
                           updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

                           CONSTRAINT pk_addresses         PRIMARY KEY (id),
                           CONSTRAINT fk_addresses_user    FOREIGN KEY (user_id)
                               REFERENCES users (id)
                               ON DELETE CASCADE
);

COMMENT ON TABLE  addresses               IS 'Saved shipping / billing addresses belonging to a user.';
COMMENT ON COLUMN addresses.label        IS 'Optional friendly name shown in the UI (Home, Office, etc.).';
COMMENT ON COLUMN addresses.recipient_name IS 'Name of the person receiving the delivery at this address.';
COMMENT ON COLUMN addresses.country      IS 'ISO 3166-1 alpha-2 country code. Defaults to KH (Cambodia).';
COMMENT ON COLUMN addresses.is_default   IS 'Only one address per user should be true. Enforced at application level.';

CREATE INDEX idx_addresses_user_id   ON addresses (user_id);
CREATE INDEX idx_addresses_is_default ON addresses (user_id, is_default);


-- -------------------------------------------------------------
--  Table: user_audit_log
--  Immutable append-only log of profile changes and key events.
--  Written by the application; never updated or deleted.
-- -------------------------------------------------------------
CREATE TABLE user_audit_log (
                                id              UUID        NOT NULL DEFAULT gen_random_uuid(),
                                user_id         UUID        NOT NULL,
                                action          VARCHAR(100) NOT NULL,  -- e.g. PROFILE_UPDATED, ADDRESS_ADDED
                                actor_id        VARCHAR(36),            -- keycloak_id of who performed the action
                                old_value       JSONB,                  -- previous state (nullable)
                                new_value       JSONB,                  -- new state (nullable)
                                ip_address      VARCHAR(45),            -- IPv4 or IPv6
                                user_agent      TEXT,
                                occurred_at     TIMESTAMP   NOT NULL DEFAULT NOW(),

                                CONSTRAINT pk_user_audit_log     PRIMARY KEY (id),
                                CONSTRAINT fk_audit_user         FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE
);

COMMENT ON TABLE  user_audit_log            IS 'Append-only audit trail for user profile and address events.';
COMMENT ON COLUMN user_audit_log.action     IS 'Event name in SCREAMING_SNAKE_CASE, e.g. PROFILE_UPDATED, ADDRESS_DELETED.';
COMMENT ON COLUMN user_audit_log.actor_id   IS 'Keycloak sub of the user or admin who triggered the action.';
COMMENT ON COLUMN user_audit_log.old_value  IS 'JSON snapshot of the entity before the change.';
COMMENT ON COLUMN user_audit_log.new_value  IS 'JSON snapshot of the entity after the change.';

CREATE INDEX idx_audit_user_id     ON user_audit_log (user_id);
CREATE INDEX idx_audit_occurred_at ON user_audit_log (occurred_at DESC);
CREATE INDEX idx_audit_action      ON user_audit_log (action);


-- -------------------------------------------------------------
--  Auto-update updated_at on users and addresses
-- -------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_addresses_updated_at
    BEFORE UPDATE ON addresses
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- -------------------------------------------------------------
--  Seed: default system user (used for internal service calls)
-- -------------------------------------------------------------
INSERT INTO users (id, keycloak_id, full_name, email, is_active)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           '00000000-0000-0000-0000-000000000001',
           'System',
           'system@tomneak.com',
           TRUE
       ) ON CONFLICT DO NOTHING;