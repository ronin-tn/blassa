-- Modify the phone_format_check constraint to allow NULL values for OAuth users
-- First drop the existing constraint, then recreate it allowing NULL

ALTER TABLE users DROP CONSTRAINT IF EXISTS phone_format_check;

-- Recreate the constraint allowing NULL values (NULL OR matches phone format)
ALTER TABLE users ADD CONSTRAINT phone_format_check 
    CHECK (phone_number IS NULL OR phone_number ~ '^\+[1-9][0-9]{7,14}$');

-- Also relax the gender constraint to allow NULL for OAuth users
ALTER TABLE users ALTER COLUMN gender DROP NOT NULL;
