ALTER TABLE users
    ADD COLUMN verification_token VARCHAR(255),
    ADD COLUMN is_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN reset_token VARCHAR(255);