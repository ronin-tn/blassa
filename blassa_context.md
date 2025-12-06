
PROJECT CONTEXT: Blassa (Production Carpooling Backend)
1. Project Overview
App Name: Blassa

Core Function: Ride-sharing platform for the Tunisian market (Cash-only, Trust-based, Gender-safe).

Tech Stack: Spring Boot 3.x, Java 17+, PostgreSQL 16 + PostGIS, Hibernate Spatial.

Auth: Stateless JWT (Spring Security).

Architecture: Monolith (Layered: Controller -> Service -> Repository -> DB).

2. Technical Constraints (Strict Rules)
IDs: All Primary Keys are UUID. No Integers.

Geography: Use geography(Point, 4326) for locations. Never use geometry or plain lat/lon doubles in the Entity.

Concurrency: Critical resources (Seats) uses Optimistic Locking (@Version).

Enums: Strict use of Enums for Gender, Role, RideStatus, BookingStatus, RideGenderPreference.

Database Casting: Connection string must include ?stringtype=unspecified to handle Enum casting.

3. Database Schema (Source of Truth)
Status: Validated & Migrated via Flyway

SQL

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ENUMS
CREATE TYPE user_gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE ride_status AS ENUM ('SCHEDULED', 'FULL', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED');
CREATE TYPE ride_gender_preference AS ENUM ('ANY', 'FEMALE_ONLY', 'MALE_ONLY');

-- TABLES
CREATE TABLE users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender user_gender NOT NULL,
    date_of_birth DATE NOT NULL,
    role user_role DEFAULT 'USER',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE rides (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES users(id),
    origin_name VARCHAR(255) NOT NULL,
    origin_point GEOGRAPHY(POINT, 4326) NOT NULL, 
    destination_name VARCHAR(255) NOT NULL,
    destination_point GEOGRAPHY(POINT, 4326) NOT NULL,
    departure_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price_per_seat NUMERIC(10, 2) NOT NULL,
    gender_preference ride_gender_preference DEFAULT 'ANY',
    status ride_status DEFAULT 'SCHEDULED',
    version INT DEFAULT 0
);

CREATE TABLE bookings (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    ride_id UUID NOT NULL REFERENCES rides(id),
    passenger_id UUID NOT NULL REFERENCES users(id),
    seats_booked INT DEFAULT 1,
    price_total NUMERIC(10, 2) NOT NULL,
    status booking_status DEFAULT 'PENDING',
    UNIQUE(ride_id, passenger_id)
);
4. Key Business Logic
A. Gender Safety (Search)
Logic:

Men searching see: ANY and MALE_ONLY. (Must NEVER see FEMALE_ONLY).

Women searching see: ANY and FEMALE_ONLY.

Implementation: Logic resides in RideService, passing a List<String> allowedPreferences to the Repository.

B. Spatial Search
Logic: Two-radius fuzzy search.

Pickup Radius: Configurable (default 3km).

Dropoff Radius: Fixed (5km).

Query: Uses ST_DWithin on the GiST index.

C. Booking Transaction (Next Step)
Goal: Prevent double-booking.

Logic:

Check if User != Driver.

Check if User hasn't already booked.

Decrement available_seats.

If available_seats == 0, set Status to FULL.

Save Ride (Trigger @Version check).

Save Booking.

5. Current Implementation Status
Phase 1 (Setup): Database, Docker, Security Config (Completed).

Phase 2 (Auth): Register/Login with JWT & Enum mapping (Completed).

Phase 3 (Entities): All JPA Entities mapped correctly with Spatial types (Completed).

Phase 4 (Rides): Create Ride, Search Ride (Pagination + Geo + Safety) (Completed).

Phase 5 (Bookings): PENDING - THIS IS THE CURRENT TASK.