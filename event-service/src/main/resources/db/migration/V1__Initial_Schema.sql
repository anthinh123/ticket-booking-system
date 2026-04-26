-- Events Table (The ONLY table owned by event-service)
CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    venue_name VARCHAR(255),
    total_seats INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    sale_start_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sale_start_time ON events (sale_start_time);
CREATE INDEX IF NOT EXISTS idx_status ON events (status);
