-- =====================================================
-- GetMyShow - Booking Module Schema
-- Version: 2.0.0
-- Description: Booking, User Management, and Payment tables
-- Author: Raju
-- Date: 2024-02-16
-- Note: seat_inventory table was created in V1 but logically
--       belongs to this Booking module
-- =====================================================

-- =====================================================
-- TABLE: USERS (User Management Module)
-- =====================================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

    -- Authentication
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,

    -- Personal Info
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       phone VARCHAR(20),

    -- Status
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    -- Activity
                       last_login_at TIMESTAMP,

    -- Timestamps
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for users
CREATE UNIQUE INDEX idx_users_email_lower ON users(LOWER(email));
CREATE INDEX idx_users_phone ON users(phone) WHERE phone IS NOT NULL;
CREATE INDEX idx_users_active ON users(is_active) WHERE is_active = TRUE;

COMMENT ON TABLE users IS 'Application users - customers and staff';

-- =====================================================
-- TABLE: ROLES
-- =====================================================
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
                                          ('CUSTOMER', 'Regular customers who book tickets'),
                                          ('STAFF', 'Venue staff who validate tickets'),
                                          ('MANAGER', 'Venue managers who manage shows and events'),
                                          ('ADMIN', 'System administrators with full access')
ON CONFLICT (name) DO NOTHING;

CREATE UNIQUE INDEX idx_roles_name_upper ON roles(UPPER(name));

COMMENT ON TABLE roles IS 'User roles for access control';

-- =====================================================
-- TABLE: USER_ROLES (Many-to-Many)
-- =====================================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            assigned_by BIGINT,

                            PRIMARY KEY (user_id, role_id),

                            CONSTRAINT fk_user_role_user FOREIGN KEY (user_id)
                                REFERENCES users(id) ON DELETE CASCADE,

                            CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
                                REFERENCES roles(id) ON DELETE CASCADE,

                            CONSTRAINT fk_user_role_assigner FOREIGN KEY (assigned_by)
                                REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

COMMENT ON TABLE user_roles IS 'User-to-role assignments';

-- =====================================================
-- TABLE: BOOKINGS (Core Booking Module)
-- =====================================================
CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,

    -- Reference
                          booking_reference VARCHAR(50) NOT NULL UNIQUE,

    -- Relationships
                          user_id BIGINT NOT NULL,
                          show_id BIGINT NOT NULL,

    -- Status
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    -- Seat Info
                          total_seats INTEGER NOT NULL CHECK (total_seats > 0),

    -- Pricing
                          total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount > 0),

    -- Lifecycle Timestamps
                          expires_at TIMESTAMP,
                          confirmed_at TIMESTAMP,
                          cancelled_at TIMESTAMP,
                          cancellation_reason TEXT,

    -- Audit
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                          CONSTRAINT fk_booking_user FOREIGN KEY (user_id)
                              REFERENCES users(id) ON DELETE RESTRICT,

                          CONSTRAINT fk_booking_show FOREIGN KEY (show_id)
                              REFERENCES shows(id) ON DELETE RESTRICT,

    -- Constraints
                          CONSTRAINT chk_booking_status CHECK (
                              status IN ('PENDING', 'CONFIRMED', 'PAYMENT_FAILED', 'CANCELLED', 'EXPIRED')
                              )
);

-- Indexes for bookings
CREATE UNIQUE INDEX idx_bookings_reference ON bookings(booking_reference);
CREATE INDEX idx_bookings_user_created ON bookings(user_id, created_at DESC);
CREATE INDEX idx_bookings_show ON bookings(show_id);
CREATE INDEX idx_bookings_status ON bookings(status);

-- Critical index for cleanup job (finding expired bookings)
CREATE INDEX idx_bookings_pending_expired ON bookings(expires_at)
    WHERE status = 'PENDING' AND expires_at IS NOT NULL;

COMMENT ON TABLE bookings IS 'Customer ticket bookings';
COMMENT ON COLUMN bookings.booking_reference IS 'User-facing booking ID (e.g., BK123456)';
COMMENT ON COLUMN bookings.expires_at IS 'When pending booking expires (typically 10-15 minutes)';

-- =====================================================
-- TABLE: BOOKING_SEATS (Seats in a Booking)
-- =====================================================
CREATE TABLE booking_seats (
                               id BIGSERIAL PRIMARY KEY,

                               booking_id BIGINT NOT NULL,
                               seat_inventory_id BIGINT NOT NULL,

    -- Denormalized for quick access
                               seat_label VARCHAR(20) NOT NULL,

    -- Pricing (captured at booking time)
                               price DECIMAL(10, 2) NOT NULL CHECK (price > 0),

                               CONSTRAINT fk_booking_seat_booking FOREIGN KEY (booking_id)
                                   REFERENCES bookings(id) ON DELETE CASCADE,

                               CONSTRAINT fk_booking_seat_inventory FOREIGN KEY (seat_inventory_id)
                                   REFERENCES seat_inventory(id) ON DELETE RESTRICT,

                               CONSTRAINT uq_seat_per_booking UNIQUE (booking_id, seat_inventory_id)
);

CREATE INDEX idx_booking_seats_booking ON booking_seats(booking_id);
CREATE INDEX idx_booking_seats_inventory ON booking_seats(seat_inventory_id);

COMMENT ON TABLE booking_seats IS 'Individual seats within a booking';
COMMENT ON COLUMN booking_seats.seat_label IS 'Denormalized seat label (e.g., A12) for display';

-- =====================================================
-- TABLE: PAYMENTS
-- =====================================================
CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,

                          booking_id BIGINT NOT NULL,

    -- Payment Details
                          payment_method VARCHAR(50) NOT NULL,
                          amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
                          currency VARCHAR(3) NOT NULL DEFAULT 'INR',

    -- Status
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    -- Gateway Integration
                          provider VARCHAR(50),
                          provider_payment_id VARCHAR(255),
                          provider_order_id VARCHAR(255),

    -- Failure Info
                          failure_reason TEXT,

    -- Timestamps
                          initiated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          completed_at TIMESTAMP,

    -- Foreign Key
                          CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id)
                              REFERENCES bookings(id) ON DELETE RESTRICT,

    -- Constraints
                          CONSTRAINT chk_payment_status CHECK (
                              status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'REFUNDED')
                              ),

                          CONSTRAINT chk_payment_method CHECK (
                              payment_method IN ('CREDIT_CARD', 'DEBIT_CARD', 'UPI', 'NET_BANKING', 'WALLET')
                              )
);

CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_provider_id ON payments(provider_payment_id);
CREATE INDEX idx_payments_status ON payments(status);

COMMENT ON TABLE payments IS 'Payment transactions for bookings';

-- =====================================================
-- TABLE: TICKETS
-- =====================================================
CREATE TABLE tickets (
                         id BIGSERIAL PRIMARY KEY,

                         ticket_number VARCHAR(50) NOT NULL UNIQUE,
                         booking_id BIGINT NOT NULL,

    -- QR Code & PDF
                         qr_code_data TEXT NOT NULL,
                         pdf_path VARCHAR(500),

    -- Validation Status
                         status VARCHAR(50) NOT NULL DEFAULT 'VALID',
                         validated_at TIMESTAMP,
                         validated_by BIGINT,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                         CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id)
                             REFERENCES bookings(id) ON DELETE RESTRICT,

                         CONSTRAINT fk_ticket_validator FOREIGN KEY (validated_by)
                             REFERENCES users(id) ON DELETE SET NULL,

    -- Constraints
                         CONSTRAINT chk_ticket_status CHECK (
                             status IN ('VALID', 'USED', 'CANCELLED', 'EXPIRED')
                             )
);

CREATE UNIQUE INDEX idx_tickets_number ON tickets(ticket_number);
CREATE INDEX idx_tickets_booking ON tickets(booking_id);
CREATE INDEX idx_tickets_status ON tickets(status);

COMMENT ON TABLE tickets IS 'Generated tickets after successful payment';

-- =====================================================
-- TRIGGERS: Auto-update timestamps
-- =====================================================

-- Reuse the function from V1
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- AUDIT LOG TABLE (Optional - for tracking)
-- =====================================================
CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,

    -- Who
                            user_id BIGINT,

    -- What
                            action_type VARCHAR(50) NOT NULL,
                            entity_type VARCHAR(50) NOT NULL,
                            entity_id BIGINT,

    -- Details
                            metadata JSONB,

    -- When
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_user FOREIGN KEY (user_id)
                                REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at DESC);
CREATE INDEX idx_audit_action ON audit_logs(action_type);

COMMENT ON TABLE audit_logs IS 'Audit trail for security and debugging';

-- =====================================================
-- HELPER FUNCTION: Generate Booking Reference
-- =====================================================
CREATE OR REPLACE FUNCTION generate_booking_reference()
    RETURNS VARCHAR AS $$
DECLARE
    new_ref VARCHAR;
    ref_exists BOOLEAN;
BEGIN
    LOOP
        -- Generate format: BK + 8 random uppercase alphanumeric
        new_ref := 'BK' || UPPER(
                SUBSTRING(MD5(RANDOM()::TEXT || CLOCK_TIMESTAMP()::TEXT) FROM 1 FOR 8)
                           );

        -- Check if exists
        SELECT EXISTS(SELECT 1 FROM bookings WHERE booking_reference = new_ref) INTO ref_exists;

        EXIT WHEN NOT ref_exists;
    END LOOP;

    RETURN new_ref;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION generate_booking_reference() IS 'Generates unique booking reference like BK12ABC34';

-- =====================================================
-- END OF V2 MIGRATION
-- Migration Info:
-- - User management tables (users, roles, user_roles)
-- - Booking core tables (bookings, booking_seats)
-- - Payment table
-- - Ticket table
-- - Audit logging support
-- - Helper function for booking reference generation
-- =====================================================