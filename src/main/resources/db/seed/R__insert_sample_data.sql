-- =====================================================
-- Sample Data for Testing
-- This is a REPEATABLE migration (runs on checksum change)
-- =====================================================

-- Clear existing sample data (safe for development)
DELETE FROM seat_inventory WHERE show_id IN (
    SELECT id FROM shows WHERE event_title LIKE '%(Sample)%'
);
DELETE FROM shows WHERE event_title LIKE '%(Sample)%';
DELETE FROM seats WHERE screen_id IN (
    SELECT id FROM screens WHERE name LIKE '%(Sample)%'
);
DELETE FROM screens WHERE name LIKE '%(Sample)%';
DELETE FROM venues WHERE name LIKE '%(Sample)%';
DELETE FROM movies WHERE event_id IN (
    SELECT id FROM events WHERE title LIKE '%(Sample)%'
);
DELETE FROM sports_events WHERE event_id IN (
    SELECT id FROM events WHERE title LIKE '%(Sample)%'
);
DELETE FROM events WHERE title LIKE '%(Sample)%';

-- Insert sample events (Movies)
INSERT INTO events (title, description, event_type, status, is_featured, tags, published_at)
VALUES
    ('Inception (Sample)', 'A thief who steals corporate secrets through dream-sharing technology', 'MOVIE', 'PUBLISHED', TRUE, ARRAY['sci-fi', 'thriller', 'action'], CURRENT_TIMESTAMP),
    ('The Dark Knight (Sample)', 'When the menace known as the Joker wreaks havoc', 'MOVIE', 'PUBLISHED', TRUE, ARRAY['action', 'crime', 'drama'], CURRENT_TIMESTAMP),
    ('Interstellar (Sample)', 'A team of explorers travel through a wormhole in space', 'MOVIE', 'PUBLISHED', FALSE, ARRAY['sci-fi', 'adventure', 'drama'], CURRENT_TIMESTAMP);

-- Insert movies
INSERT INTO movies (event_id, duration, genre, language, director, censor_rating, imdb_rating, release_date, formats)
SELECT
    id,
    148,
    'Sci-Fi',
    'English',
    'Christopher Nolan',
    'UA',
    8.8,
    '2010-07-16',
    ARRAY['2D', '3D', 'IMAX']
FROM events WHERE title = 'Inception (Sample)';

INSERT INTO movies (event_id, duration, genre, language, director, censor_rating, imdb_rating, release_date, formats)
SELECT
    id,
    152,
    'Action',
    'English',
    'Christopher Nolan',
    'UA',
    9.0,
    '2008-07-18',
    ARRAY['2D', 'IMAX']
FROM events WHERE title = 'The Dark Knight (Sample)';

-- Insert sports event
INSERT INTO events (title, description, event_type, status, is_featured, tags, published_at)
VALUES
    ('India vs Australia (Sample)', 'ICC World Cup Final 2024', 'SPORTS', 'PUBLISHED', TRUE, ARRAY['cricket', 'world-cup', 'final'], CURRENT_TIMESTAMP);

INSERT INTO sports_events (event_id, sport_type, team_home, team_away, tournament_name, match_type, is_international)
SELECT
    id,
    'CRICKET',
    'India',
    'Australia',
    'ICC World Cup 2024',
    'FINAL',
    TRUE
FROM events WHERE title = 'India vs Australia (Sample)';

-- Insert venue
INSERT INTO venues (name, venue_type, city, area, address, total_screens, total_capacity, facilities, status)
VALUES
    ('PVR Phoenix (Sample)', 'CINEMA', 'Mumbai', 'Lower Parel', 'Phoenix Marketcity, Mumbai', 5, 1200,
     '["Parking", "Food Court", "Wheelchair Access", "IMAX", "Dolby Atmos"]'::JSONB, 'ACTIVE'),
    ('Wankhede Stadium (Sample)', 'STADIUM', 'Mumbai', 'Churchgate', 'D Road, Churchgate, Mumbai', 1, 33000,
     '["Parking", "Food Stalls", "VIP Boxes", "Press Box"]'::JSONB, 'ACTIVE');

-- Insert screens
INSERT INTO screens (venue_id, name, screen_type, total_seats, total_rows, seats_per_row, screen_format, status)
SELECT
    id,
    'Screen 1 (Sample)',
    'IMAX',
    200,
    10,
    20,
    'IMAX',
    'ACTIVE'
FROM venues WHERE name = 'PVR Phoenix (Sample)';

-- Insert seats (generate 200 seats: rows A-J, seats 1-20)
INSERT INTO seats (screen_id, row_label, seat_number, seat_type, display_label)
SELECT
    s.id,
    chr(64 + row_num),  -- A, B, C... J
    seat_num,
    CASE
        WHEN row_num <= 3 THEN 'REGULAR'
        WHEN row_num <= 7 THEN 'PREMIUM'
        ELSE 'VIP'
        END,
    chr(64 + row_num) || seat_num
FROM
    screens s
        CROSS JOIN generate_series(1, 10) AS row_num
        CROSS JOIN generate_series(1, 20) AS seat_num
WHERE s.name = 'Screen 1 (Sample)';

COMMENT ON TABLE events IS 'Updated: Sample data added for testing';