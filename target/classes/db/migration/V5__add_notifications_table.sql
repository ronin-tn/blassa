-- Migration: Add notifications table for real-time notification system

CREATE TABLE IF NOT EXISTS notifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         TEXT NOT NULL,
    link            VARCHAR(500),
    is_read         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Index for efficient lookup of unread notifications per user
CREATE INDEX IF NOT EXISTS idx_notifications_recipient_unread 
    ON notifications(recipient_id, is_read) 
    WHERE is_read = FALSE;

-- Index for ordering by creation time
CREATE INDEX IF NOT EXISTS idx_notifications_created_at 
    ON notifications(created_at DESC);

