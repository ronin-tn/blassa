-- =============================================
-- BLASSA DATABASE - COMPLETE NEON MIGRATION
-- Run this script in your Neon console to set up the entire database
-- =============================================

-- =============================================
-- 1. ENABLE EXTENSIONS
-- =============================================
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- 2. CREATE CUSTOM ENUM TYPES
-- =============================================
CREATE TYPE user_gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE ride_status AS ENUM ('SCHEDULED', 'FULL', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED');
CREATE TYPE ride_gender_preference AS ENUM ('ANY', 'FEMALE_ONLY', 'MALE_ONLY');

-- =============================================
-- 3. CREATE TABLES
-- =============================================

-- Users table
CREATE TABLE users (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email                 VARCHAR(255) NOT NULL UNIQUE,
    password_hash         VARCHAR(255),  -- Nullable for OAuth users
    phone_number          VARCHAR(20) UNIQUE,  -- Nullable for OAuth users
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100) NOT NULL,
    gender                user_gender,  -- Nullable for OAuth users
    date_of_birth         DATE,  -- Nullable for OAuth users
    bio                   TEXT,
    profile_picture_url   TEXT,
    cin_number            VARCHAR(20) UNIQUE,
    email_verified        BOOLEAN DEFAULT FALSE,
    role                  user_role DEFAULT 'USER',
    -- Verification fields
    verification_token    VARCHAR(255),
    is_verified           BOOLEAN DEFAULT FALSE,
    reset_token           VARCHAR(255),
    verification_sent_at  TIMESTAMP,
    -- Social media fields
    facebook_url          VARCHAR(255),
    instagram_url         VARCHAR(255),
    -- OAuth fields
    oauth_provider        VARCHAR(50),
    oauth_id              VARCHAR(255),
    -- Timestamps
    created_at            TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at            TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    deleted_at            TIMESTAMP WITHOUT TIME ZONE,
    -- Constraints
    CONSTRAINT phone_format_check CHECK (phone_number IS NULL OR phone_number ~ '^\+[1-9][0-9]{7,14}$')
);

-- Vehicles table
CREATE TABLE vehicles (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id        UUID NOT NULL,
    make            VARCHAR(50) NOT NULL,
    model           VARCHAR(50) NOT NULL,
    color           VARCHAR(30) NOT NULL,
    license_plate   VARCHAR(20) NOT NULL UNIQUE,
    production_year INTEGER,
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Rides table
CREATE TABLE rides (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    driver_id         UUID NOT NULL,
    vehicle_id        UUID,
    origin_name       VARCHAR(255) NOT NULL,
    origin_point      GEOGRAPHY NOT NULL,
    destination_name  VARCHAR(255) NOT NULL,
    destination_point GEOGRAPHY NOT NULL,
    departure_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    total_seats       INTEGER NOT NULL,
    available_seats   INTEGER NOT NULL,
    price_per_seat    NUMERIC(10, 2) NOT NULL,
    gender_preference ride_gender_preference DEFAULT 'ANY',
    allows_smoking    BOOLEAN DEFAULT FALSE,
    status            ride_status DEFAULT 'SCHEDULED',
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    version           INTEGER DEFAULT 0,
    CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE NO ACTION,
    CONSTRAINT fk_ride_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Bookings table
CREATE TABLE bookings (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ride_id      UUID NOT NULL,
    passenger_id UUID NOT NULL,
    seats_booked INTEGER DEFAULT 1,
    price_total  NUMERIC(10, 2) NOT NULL,
    status       booking_status DEFAULT 'PENDING',
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT bookings_ride_id_passenger_id_key UNIQUE (ride_id, passenger_id),
    CONSTRAINT fk_booking_ride FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE NO ACTION,
    CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE NO ACTION
);

-- Reviews table
CREATE TABLE reviews (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id  UUID NOT NULL,
    reviewer_id UUID NOT NULL,
    reviewee_id UUID NOT NULL,
    rating      INTEGER NOT NULL,
    comment     TEXT,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT reviews_booking_id_reviewer_id_key UNIQUE (booking_id, reviewer_id),
    CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE NO ACTION,
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE NO ACTION,
    CONSTRAINT fk_review_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id) ON DELETE NO ACTION
);

-- Notifications table
CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         TEXT NOT NULL,
    link            VARCHAR(500),
    is_read         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- =============================================
-- 4. CREATE INDEXES
-- =============================================

-- User indexes
CREATE INDEX IF NOT EXISTS idx_users_verification_sent_at ON users(verification_sent_at);
CREATE INDEX IF NOT EXISTS idx_users_oauth ON users(oauth_provider, oauth_id);

-- Notification indexes
CREATE INDEX IF NOT EXISTS idx_notifications_recipient_unread 
    ON notifications(recipient_id, is_read) 
    WHERE is_read = FALSE;
CREATE INDEX IF NOT EXISTS idx_notifications_created_at 
    ON notifications(created_at DESC);

-- =============================================
-- 5. INSERT SRID 4326 FOR POSTGIS (GPS COORDINATES)
-- =============================================
INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text)
SELECT 4326, 'EPSG', 4326, 
    'GEOGCS["WGS 84",DATUM["WGS_1984",SPHEROID["WGS 84",6378137,298.257223563,AUTHORITY["EPSG","7030"]],AUTHORITY["EPSG","6326"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.0174532925199433,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4326"]]', 
    '+proj=longlat +datum=WGS84 +no_defs'
WHERE NOT EXISTS (SELECT 1 FROM spatial_ref_sys WHERE srid = 4326);
