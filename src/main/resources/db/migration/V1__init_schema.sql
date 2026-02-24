-- =====================================================
-- GetMyShow - Event Catalog Schema
-- Version: 1.0.0
-- Description: Core tables for multi-event booking system
-- Author: Raju
-- Date: 2024-02-16
-- Supports: Movies, Sports, and extensible event types
-- Performance: Optimized for 10K concurrent users
-- =====================================================

-- =====================================================
-- TABLE: EVENTS (Base table for all event types)
-- =====================================================
CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,

    -- Basic Information
                        title VARCHAR(255) NOT NULL,
                        description TEXT,

    -- Event Type Discrimination
                        event_type VARCHAR(50) NOT NULL,

    -- Media URLs
                        poster_url VARCHAR(500),
                        banner_url VARCHAR(500),
                        thumbnail_url VARCHAR(500),

    -- Status & Visibility
                        status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
                        is_featured BOOLEAN DEFAULT FALSE,

    -- Metadata
                        tags TEXT[],

    -- Timestamps
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        published_at TIMESTAMP,

    -- Constraints
                        CONSTRAINT chk_event_type CHECK (
                            event_type IN ('MOVIE', 'SPORTS', 'CONCERT', 'THEATRE', 'STANDUP')
                            ),
                        CONSTRAINT chk_event_status CHECK (
                            status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'CANCELLED')
                            )
);

-- Indexes for events
CREATE INDEX idx_events_type_status ON events(event_type, status);
CREATE INDEX idx_events_featured ON events(is_featured) WHERE is_featured = TRUE;
CREATE INDEX idx_events_published ON events(published_at DESC) WHERE status = 'PUBLISHED';
CREATE INDEX idx_events_tags ON events USING GIN(tags); --GIN (Generalized Inverted Index)

COMMENT ON TABLE events IS 'Base table for all event types (polymorphic)';
COMMENT ON COLUMN events.tags IS 'Searchable tags array for content discovery';
COMMENT ON COLUMN events.event_type IS 'Discriminator column for event type hierarchy';

-- =====================================================
-- TABLE: MOVIES (Movie-specific details)
-- =====================================================
CREATE TABLE movies (
                        id BIGSERIAL PRIMARY KEY,
                        event_id BIGINT NOT NULL UNIQUE,

    -- Movie Details
                        duration INTEGER NOT NULL,
                        genre VARCHAR(100) NOT NULL,
                        language VARCHAR(50) NOT NULL,

    -- Cast & Crew (JSONB for flexibility)
                        director VARCHAR(255),
                        cast_members JSONB,
                        crew JSONB,

    -- Ratings
                        censor_rating VARCHAR(10),
                        imdb_rating DECIMAL(3, 1),
                        user_rating DECIMAL(3, 1),

    -- Release Information
                        release_date DATE,
                        original_language VARCHAR(50),
                        country_of_origin VARCHAR(100),

    -- Technical Details
                        formats TEXT[],

    -- Timestamps
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                        CONSTRAINT fk_movie_event FOREIGN KEY (event_id)
                            REFERENCES events(id) ON DELETE CASCADE,

    -- Constraints
                        CONSTRAINT chk_duration CHECK (duration > 0 AND duration < 500),
                        CONSTRAINT chk_censor_rating CHECK (
                            censor_rating IN ('U', 'UA', 'A', 'S')
                            ),
                        CONSTRAINT chk_imdb_rating CHECK (
                            imdb_rating >= 0 AND imdb_rating <= 10
                            ),
                        CONSTRAINT chk_user_rating CHECK (
                            user_rating >= 0 AND user_rating <= 10
                            )
);

-- Indexes for movies
CREATE INDEX idx_movies_event ON movies(event_id);
CREATE INDEX idx_movies_genre ON movies(genre);
CREATE INDEX idx_movies_language ON movies(language);
CREATE INDEX idx_movies_rating ON movies(imdb_rating DESC) WHERE imdb_rating IS NOT NULL;
CREATE INDEX idx_movies_release ON movies(release_date DESC);
CREATE INDEX idx_movies_cast_members ON movies USING GIN(cast_members);
CREATE INDEX idx_movies_formats ON movies USING GIN(formats);

COMMENT ON TABLE movies IS 'Movie-specific details extending events table';
COMMENT ON COLUMN movies.cast_members IS 'JSONB: [{"name": "Actor", "role": "Character"}]';
COMMENT ON COLUMN movies.formats IS 'Available formats: 2D, 3D, IMAX, etc.';

-- =====================================================
-- TABLE: SPORTS_EVENTS (Sports-specific details)
-- =====================================================
CREATE TABLE sports_events (
                               id BIGSERIAL PRIMARY KEY,
                               event_id BIGINT NOT NULL UNIQUE,

    -- Sports Details
                               sport_type VARCHAR(50) NOT NULL,

    -- Teams/Players
                               team_home VARCHAR(255),
                               team_away VARCHAR(255),
                               team_home_logo VARCHAR(500),
                               team_away_logo VARCHAR(500),

    -- Context
                               tournament_name VARCHAR(255),
                               match_type VARCHAR(50),
                               venue_type VARCHAR(50) DEFAULT 'STADIUM',

    -- Timing
                               expected_duration INTEGER,

    -- Additional Info
                               is_international BOOLEAN DEFAULT FALSE,
                               league_name VARCHAR(255),
                               season VARCHAR(50),

    -- Timestamps
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                               CONSTRAINT fk_sports_event FOREIGN KEY (event_id)
                                   REFERENCES events(id) ON DELETE CASCADE,

    -- Constraints
                               CONSTRAINT chk_sport_type CHECK (
                                   sport_type IN ('CRICKET', 'FOOTBALL', 'BASKETBALL', 'TENNIS', 'BADMINTON', 'HOCKEY')
                                   ),
                               CONSTRAINT chk_match_type CHECK (
                                   match_type IN ('TEST', 'ODI', 'T20', 'LEAGUE', 'FRIENDLY', 'PLAYOFF', 'FINAL')
                                   )
);

-- Indexes for sports_events
CREATE INDEX idx_sports_event ON sports_events(event_id);
CREATE INDEX idx_sports_type ON sports_events(sport_type);
CREATE INDEX idx_sports_tournament ON sports_events(tournament_name);
CREATE INDEX idx_sports_teams ON sports_events(team_home, team_away);

COMMENT ON TABLE sports_events IS 'Sports-specific details extending events table';

-- =====================================================
-- TABLE: VENUES (Physical locations)
-- =====================================================
CREATE TABLE venues (
                        id BIGSERIAL PRIMARY KEY,

    -- Basic Information
                        name VARCHAR(255) NOT NULL,
                        venue_type VARCHAR(50) NOT NULL,

    -- Location
                        city VARCHAR(100) NOT NULL,
                        area VARCHAR(100),
                        address TEXT NOT NULL,
                        pincode VARCHAR(10),
                        state VARCHAR(100),
                        country VARCHAR(100) DEFAULT 'India',

    -- Geolocation
                        latitude DECIMAL(10, 8),
                        longitude DECIMAL(11, 8),

    -- Capacity
                        total_screens INTEGER NOT NULL DEFAULT 1,
                        total_capacity INTEGER,

    -- Facilities
                        facilities JSONB,

    -- Contact
                        phone VARCHAR(20),
                        email VARCHAR(255),
                        website VARCHAR(500),

    -- Operational
                        status VARCHAR(50) DEFAULT 'ACTIVE',
                        opening_time TIME,
                        closing_time TIME,

    -- Timestamps
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
                        CONSTRAINT chk_venue_type CHECK (
                            venue_type IN ('CINEMA', 'STADIUM', 'ARENA', 'THEATRE', 'AUDITORIUM')
                            ),
                        CONSTRAINT chk_venue_status CHECK (
                            status IN ('ACTIVE', 'INACTIVE', 'UNDER_MAINTENANCE', 'CLOSED')
                            ),
                        CONSTRAINT chk_total_screens CHECK (total_screens > 0),
                        CONSTRAINT chk_coordinates CHECK (
                            (latitude IS NULL AND longitude IS NULL) OR
                            (latitude IS NOT NULL AND longitude IS NOT NULL)
                            )
);

-- Indexes for venues (CRITICAL for search)
CREATE INDEX idx_venues_city ON venues(city);
CREATE INDEX idx_venues_city_type ON venues(city, venue_type);
CREATE INDEX idx_venues_status ON venues(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_venues_facilities ON venues USING GIN(facilities);

COMMENT ON TABLE venues IS 'Physical locations: cinemas, stadiums, theaters';
COMMENT ON COLUMN venues.facilities IS 'JSONB: ["Parking", "Food Court", "3D"]';

-- =====================================================
-- TABLE: SCREENS (Halls/Sections within venues)
-- =====================================================
CREATE TABLE screens (
                         id BIGSERIAL PRIMARY KEY,
                         venue_id BIGINT NOT NULL,

    -- Screen Details
                         name VARCHAR(100) NOT NULL,
                         screen_type VARCHAR(50) DEFAULT 'STANDARD',

    -- Capacity
                         total_seats INTEGER NOT NULL,
                         total_rows INTEGER NOT NULL,
                         seats_per_row INTEGER,

    -- Technical Specs
                         screen_format VARCHAR(50),
                         sound_system VARCHAR(50),

    -- Layout
                         layout_type VARCHAR(50) DEFAULT 'STANDARD',

    -- Status
                         status VARCHAR(50) DEFAULT 'ACTIVE',

    -- Timestamps
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                         CONSTRAINT fk_screen_venue FOREIGN KEY (venue_id)
                             REFERENCES venues(id) ON DELETE CASCADE,

    -- Constraints
                         CONSTRAINT uq_screen_name_per_venue UNIQUE (venue_id, name),
                         CONSTRAINT chk_screen_type CHECK (
                             screen_type IN ('STANDARD', 'IMAX', 'DOLBY', '4DX', '3D', 'STADIUM_SECTION')
                             ),
                         CONSTRAINT chk_layout_type CHECK (
                             layout_type IN ('STANDARD', 'STADIUM', 'THEATRE', 'CUSTOM')
                             ),
                         CONSTRAINT chk_total_seats CHECK (total_seats > 0),
                         CONSTRAINT chk_total_rows CHECK (total_rows > 0),
                         CONSTRAINT chk_screen_status CHECK (
                             status IN ('ACTIVE', 'MAINTENANCE', 'CLOSED')
                             )
);

-- Indexes for screens
CREATE INDEX idx_screens_venue ON screens(venue_id);
CREATE INDEX idx_screens_status ON screens(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_screens_type ON screens(screen_type);

COMMENT ON TABLE screens IS 'Individual halls/sections within venues';

-- =====================================================
-- TABLE: SEATS (Physical seat layout template)
-- =====================================================
CREATE TABLE seats (
                       id BIGSERIAL PRIMARY KEY,
                       screen_id BIGINT NOT NULL,

    -- Seat Position
                       row_label VARCHAR(5) NOT NULL,
                       seat_number INTEGER NOT NULL,

    -- Seat Classification
                       seat_type VARCHAR(50) NOT NULL DEFAULT 'REGULAR',

    -- Physical Properties
                       is_accessible BOOLEAN DEFAULT FALSE,
                       is_aisle BOOLEAN DEFAULT FALSE,

    -- Display
                       display_label VARCHAR(10),

    -- Status
                       is_blocked BOOLEAN DEFAULT FALSE,

    -- Timestamps
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                       CONSTRAINT fk_seat_screen FOREIGN KEY (screen_id)
                           REFERENCES screens(id) ON DELETE CASCADE,

    -- Constraints
                       CONSTRAINT uq_seat_per_screen UNIQUE (screen_id, row_label, seat_number),
                       CONSTRAINT chk_seat_type CHECK (
                           seat_type IN ('REGULAR', 'PREMIUM', 'VIP', 'RECLINER', 'WHEELCHAIR', 'COUPLE')
                           ),
                       CONSTRAINT chk_seat_number CHECK (seat_number > 0)
);

-- Indexes for seats
CREATE INDEX idx_seats_screen ON seats(screen_id);
CREATE INDEX idx_seats_type ON seats(seat_type);
CREATE INDEX idx_seats_row ON seats(screen_id, row_label);

COMMENT ON TABLE seats IS 'Reusable seat layout template per screen';
COMMENT ON COLUMN seats.is_blocked IS 'Permanently blocked (broken/maintenance)';

-- =====================================================
-- TABLE: SHOWS (Events scheduled at specific times)
-- =====================================================
CREATE TABLE shows (
                       id BIGSERIAL PRIMARY KEY,

    -- Relationships
                       event_id BIGINT NOT NULL,
                       screen_id BIGINT NOT NULL,

    -- Denormalized fields (PERFORMANCE OPTIMIZATION)
                       city VARCHAR(100) NOT NULL,
                       event_title VARCHAR(255) NOT NULL,
                       venue_name VARCHAR(255) NOT NULL,
                       event_type VARCHAR(50) NOT NULL,

    -- Timing
                       start_time TIMESTAMP NOT NULL,
                       end_time TIMESTAMP NOT NULL,

    -- Pricing
                       base_price DECIMAL(10, 2) NOT NULL,

    -- Availability
                       total_seats INTEGER NOT NULL,
                       available_seats INTEGER NOT NULL,

    -- Status
                       status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',

    -- Booking Window
                       booking_opens_at TIMESTAMP,
                       booking_closes_at TIMESTAMP,

    -- Timestamps
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                       CONSTRAINT fk_show_event FOREIGN KEY (event_id)
                           REFERENCES events(id) ON DELETE RESTRICT,
                       CONSTRAINT fk_show_screen FOREIGN KEY (screen_id)
                           REFERENCES screens(id) ON DELETE RESTRICT,

    -- Constraints
                       CONSTRAINT chk_show_status CHECK (
                           status IN ('SCHEDULED', 'BOOKING_OPEN', 'BOOKING_CLOSED', 'LIVE', 'COMPLETED', 'CANCELLED')
                           ),
                       CONSTRAINT chk_show_times CHECK (end_time > start_time),
                       CONSTRAINT chk_base_price CHECK (base_price >= 0),
                       CONSTRAINT chk_seat_counts CHECK (
                           available_seats >= 0 AND available_seats <= total_seats
                           ),
                       CONSTRAINT chk_booking_window CHECK (
                           booking_closes_at IS NULL OR booking_closes_at <= start_time
                           )
);

-- CRITICAL PERFORMANCE INDEXES (for < 1 sec search)
CREATE INDEX idx_shows_search_primary ON shows(city, start_time, status, event_type)
    WHERE status IN ('SCHEDULED', 'BOOKING_OPEN');

CREATE INDEX idx_shows_status_time ON shows(status, start_time);

CREATE INDEX idx_shows_event ON shows(event_id, start_time);

CREATE INDEX idx_shows_screen ON shows(screen_id, start_time);

CREATE INDEX idx_shows_availability ON shows(status, available_seats)
    WHERE available_seats > 0;

CREATE INDEX idx_shows_upcoming ON shows(start_time, status)
    WHERE status IN ('SCHEDULED', 'BOOKING_OPEN');

CREATE INDEX idx_shows_city ON shows(city, start_time);

COMMENT ON TABLE shows IS 'Events scheduled at specific venues and times';
COMMENT ON COLUMN shows.city IS 'Denormalized for sub-second search performance';
COMMENT ON COLUMN shows.available_seats IS 'Updated on booking/cancellation';

-- =====================================================
-- TABLE: SEAT_INVENTORY (Real-time seat availability)
-- =====================================================
CREATE TABLE seat_inventory (
                                id BIGSERIAL PRIMARY KEY,
                                show_id BIGINT NOT NULL,
                                seat_id BIGINT NOT NULL,

    -- Pricing
                                price DECIMAL(10, 2) NOT NULL,

    -- Status Management
                                status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    -- Concurrency Control
                                version BIGINT NOT NULL DEFAULT 0,
                                locked_at TIMESTAMP,
                                locked_by VARCHAR(255),
                                lock_expires_at TIMESTAMP,

    -- Booking Reference
                                booking_id BIGINT,

    -- Timestamps
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                                CONSTRAINT fk_inventory_show FOREIGN KEY (show_id)
                                    REFERENCES shows(id) ON DELETE CASCADE,
                                CONSTRAINT fk_inventory_seat FOREIGN KEY (seat_id)
                                    REFERENCES seats(id) ON DELETE RESTRICT,

    -- Constraints
                                CONSTRAINT uq_seat_per_show UNIQUE (show_id, seat_id),
                                CONSTRAINT chk_inventory_status CHECK (
                                    status IN ('AVAILABLE', 'LOCKED', 'BOOKED', 'BLOCKED')
                                    ),
                                CONSTRAINT chk_inventory_price CHECK (price >= 0),
                                CONSTRAINT chk_lock_consistency CHECK (
                                    (status = 'LOCKED' AND locked_at IS NOT NULL AND lock_expires_at IS NOT NULL) OR
                                    (status != 'LOCKED')
                                    ),
                                CONSTRAINT chk_booking_consistency CHECK (
                                    (status = 'BOOKED' AND booking_id IS NOT NULL) OR
                                    (status != 'BOOKED')
                                    )
);

-- PERFORMANCE-CRITICAL INDEXES (HOT table - 10K concurrent queries)
CREATE INDEX idx_inventory_show_status ON seat_inventory(show_id, status);

CREATE INDEX idx_inventory_available ON seat_inventory(show_id, seat_id)
    WHERE status = 'AVAILABLE';

CREATE INDEX idx_inventory_expired_locks ON seat_inventory(status, lock_expires_at)
    WHERE status = 'LOCKED';

CREATE INDEX idx_inventory_locked_by ON seat_inventory(locked_by, show_id)
    WHERE status = 'LOCKED';

CREATE INDEX idx_inventory_show ON seat_inventory(show_id);

COMMENT ON TABLE seat_inventory IS 'HOT table: Real-time seat status per show';
COMMENT ON COLUMN seat_inventory.version IS 'Optimistic locking for race condition prevention';
COMMENT ON COLUMN seat_inventory.lock_expires_at IS 'TTL for automatic lock release';

-- =====================================================
-- TRIGGERS: Auto-update timestamps
-- =====================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers
CREATE TRIGGER trg_events_updated_at
    BEFORE UPDATE ON events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_movies_updated_at
    BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_sports_events_updated_at
    BEFORE UPDATE ON sports_events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_venues_updated_at
    BEFORE UPDATE ON venues
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_screens_updated_at
    BEFORE UPDATE ON screens
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_shows_updated_at
    BEFORE UPDATE ON shows
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_seat_inventory_updated_at
    BEFORE UPDATE ON seat_inventory
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- END OF V1 MIGRATION
-- Migration Info:
-- - 8 core tables created
-- - 35+ performance indexes created
-- - 7 automatic timestamp triggers created
-- - Optimized for 10K concurrent users
-- - Support for movies and sports events
-- - Extensible for additional event types
-- - Zero tolerance for double booking (via locking)
-- - Sub-second search performance (denormalized fields)
-- =====================================================