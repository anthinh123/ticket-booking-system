-- Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    payment_id VARCHAR(100),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_user_id ON bookings (user_id);
CREATE INDEX IF NOT EXISTS idx_booking_reference ON bookings (booking_reference);
CREATE INDEX IF NOT EXISTS idx_status ON bookings (status);

-- Booking Seats Table
CREATE TABLE IF NOT EXISTS booking_seats (
    booking_seat_id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT uk_booking_seat UNIQUE (booking_id, seat_id)
);

CREATE INDEX IF NOT EXISTS idx_seat_id ON booking_seats (seat_id);
