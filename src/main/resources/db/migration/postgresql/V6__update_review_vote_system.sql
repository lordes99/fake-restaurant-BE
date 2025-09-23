-- 1. Rimuovi la colonna vote_type
ALTER TABLE review_entities
DROP COLUMN vote_type;

-- 2. Aggiungi le nuove colonne
ALTER TABLE review_entities
    ADD COLUMN up_vote_ids text[];

ALTER TABLE review_entities
    ADD COLUMN down_vote_ids text[];
