CREATE TABLE user_reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id UUID NOT NULL,
    reported_user_id UUID,
    ride_id UUID,
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reported_user FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_ride FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE SET NULL
);
