-- Add OAuth2 provider columns for Google login support
ALTER TABLE users ADD COLUMN IF NOT EXISTS oauth_provider VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS oauth_id VARCHAR(255);

-- Make password_hash nullable for OAuth users who don't have passwords
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;

-- Make phone_number nullable for OAuth users (they can add it later)
ALTER TABLE users ALTER COLUMN phone_number DROP NOT NULL;

-- Make date_of_birth nullable for OAuth users (they can add it later)
ALTER TABLE users ALTER COLUMN date_of_birth DROP NOT NULL;

-- Create index for faster OAuth lookups
CREATE INDEX IF NOT EXISTS idx_users_oauth ON users(oauth_provider, oauth_id);
