-- Seed Events
INSERT INTO events (event_id, event_name, event_date, venue_name, total_seats, status, sale_start_time)
VALUES 
(1, 'Taylor Swift - The Eras Tour', '2026-06-15 19:00:00', 'Wembley Stadium', 90000, 'ON_SALE', '2025-12-01 10:00:00'),
(2, 'Coldplay - Music of the Spheres', '2026-07-20 20:00:00', 'Stade de France', 80000, 'UPCOMING', '2026-01-15 09:00:00'),
(3, 'Linkin Park - Reunion Tour', '2026-08-05 18:30:00', 'Madison Square Garden', 20000, 'ON_SALE', '2025-11-20 12:00:00'),
(4, 'Blackpink - World Tour [Born Pink]', '2026-09-12 19:30:00', 'Rajamangala Stadium', 50000, 'SOLD_OUT', '2025-10-01 10:00:00')
ON CONFLICT (event_id) DO NOTHING;
