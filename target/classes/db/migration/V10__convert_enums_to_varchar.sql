-- V10: Convert PostgreSQL native ENUM types to VARCHAR
-- This fixes Hibernate 7 compatibility issues with PostgreSQL ENUMs

-- 1. Alter users table columns
ALTER TABLE users 
    ALTER COLUMN gender TYPE VARCHAR(10) USING gender::VARCHAR,
    ALTER COLUMN role TYPE VARCHAR(10) USING role::VARCHAR;

-- 2. Alter rides table columns
ALTER TABLE rides 
    ALTER COLUMN status TYPE VARCHAR(20) USING status::VARCHAR,
    ALTER COLUMN gender_preference TYPE VARCHAR(20) USING gender_preference::VARCHAR;

-- 3. Alter bookings table columns
ALTER TABLE bookings 
    ALTER COLUMN status TYPE VARCHAR(20) USING status::VARCHAR;

-- 4. Drop the old ENUM types (optional, but keeps schema clean)
DROP TYPE IF EXISTS user_gender CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS ride_status CASCADE;
DROP TYPE IF EXISTS booking_status CASCADE;
DROP TYPE IF EXISTS ride_gender_preference CASCADE;
