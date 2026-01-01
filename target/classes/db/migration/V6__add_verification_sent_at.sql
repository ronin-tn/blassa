-- Add verification_sent_at column to track when last verification email was sent
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_sent_at TIMESTAMP;

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_verification_sent_at ON users(verification_sent_at);
