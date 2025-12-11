-- V2: Add social media fields to users table
-- Allows drivers to add their Facebook and Instagram profile links

ALTER TABLE users ADD COLUMN facebook_url VARCHAR(255);
ALTER TABLE users ADD COLUMN instagram_url VARCHAR(255);
