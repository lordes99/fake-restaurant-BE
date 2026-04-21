-- Rimuovi colonna non piu' usata in ReviewEntity
ALTER TABLE review_entities
    DROP COLUMN IF EXISTS updated_at;
