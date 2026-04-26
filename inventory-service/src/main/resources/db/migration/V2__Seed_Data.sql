-- Seed seats for Event 1 (Taylor Swift)
INSERT INTO seats (event_id, seat_number, section, row_number, seat_type, price, status)
VALUES 
(1, 'A-01', 'VIP Front', '1', 'VIP', 500.00, 'AVAILABLE'),
(1, 'A-02', 'VIP Front', '1', 'VIP', 500.00, 'AVAILABLE'),
(1, 'B-10', 'General Floor', '10', 'REGULAR', 150.00, 'AVAILABLE'),
(1, 'B-11', 'General Floor', '10', 'REGULAR', 150.00, 'AVAILABLE'),
(1, 'C-05', 'Premium Stand', '5', 'PREMIUM', 250.00, 'AVAILABLE'),
(1, 'C-06', 'Premium Stand', '5', 'PREMIUM', 250.00, 'AVAILABLE')
ON CONFLICT (event_id, seat_number) DO NOTHING;

-- Seed seats for Event 2 (Coldplay)
INSERT INTO seats (event_id, seat_number, section, row_number, seat_type, price, status)
VALUES 
(2, 'VIP-01', 'Stage Front', '1', 'VIP', 450.00, 'AVAILABLE'),
(2, 'VIP-02', 'Stage Front', '1', 'VIP', 450.00, 'AVAILABLE'),
(2, 'G-20', 'Main Pit', '20', 'REGULAR', 120.00, 'AVAILABLE')
ON CONFLICT (event_id, seat_number) DO NOTHING;
