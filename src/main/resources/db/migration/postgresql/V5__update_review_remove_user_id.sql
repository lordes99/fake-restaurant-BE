-- Rimuovi colonna rating
ALTER TABLE review_entities
    DROP COLUMN IF EXISTS user_id;

