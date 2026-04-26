-- Seats Table (Inventory)
CREATE TABLE IF NOT EXISTS seats (
    seat_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,           -- reference by ID (no cross-DB FK)
    seat_number VARCHAR(20) NOT NULL,
    section VARCHAR(50),
    row_number VARCHAR(10),
    seat_type VARCHAR(50) DEFAULT 'REGULAR',
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    version BIGINT DEFAULT 0,
    reserved_by VARCHAR(50),
    reserved_until TIMESTAMP,
    booking_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_event_seat UNIQUE (event_id, seat_number)
);

CREATE INDEX IF NOT EXISTS idx_event_status ON seats (event_id, status);
CREATE INDEX IF NOT EXISTS idx_reserved_until ON seats (reserved_until);

-- Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id BIGSERIAL PRIMARY KEY,
    seat_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_seat_id ON reservations (seat_id);
CREATE INDEX IF NOT EXISTS idx_expires_at ON reservations (expires_at);
CREATE INDEX IF NOT EXISTS idx_user_id ON reservations (user_id);
