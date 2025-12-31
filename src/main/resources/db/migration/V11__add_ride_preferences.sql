-- Add new ride preference columns
ALTER TABLE rides
ADD COLUMN allows_music BOOLEAN DEFAULT FALSE NOT NULL,
ADD COLUMN allows_pets BOOLEAN DEFAULT FALSE NOT NULL,
ADD COLUMN luggage_size VARCHAR(20) DEFAULT 'MEDIUM' NOT NULL;

-- Add check constraint for luggage_size
ALTER TABLE rides
ADD CONSTRAINT chk_luggage_size CHECK (luggage_size IN ('SMALL', 'MEDIUM', 'LARGE'));
