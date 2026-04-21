-- Drop foreign keys e indice univoco
ALTER TABLE vote_entities DROP CONSTRAINT IF EXISTS fk_vote_review;
ALTER TABLE vote_entities DROP CONSTRAINT IF EXISTS fk_vote_user;
DROP INDEX IF EXISTS uq_vote_user_review;

-- Drop tabella vote_entities
DROP TABLE IF EXISTS vote_entities;

-- Rename user_id to owner_user_id
ALTER TABLE review_entities
    RENAME COLUMN user_id TO owner_user_id;

-- Aggiungi colonne mancanti
ALTER TABLE review_entities
    ADD COLUMN photos text[],
    ADD COLUMN title VARCHAR(511),
    ADD COLUMN description VARCHAR(1027);

-- Aggiungi colonna vote_type
ALTER TABLE review_entities
    ADD COLUMN vote_type VARCHAR(10) CHECK (vote_type IN ('UP', 'DOWN'));

-- Rimuovi colonna content
ALTER TABLE review_entities
    DROP COLUMN IF EXISTS content;

-- Rimuovi colonna rating
ALTER TABLE review_entities
    DROP COLUMN IF EXISTS rating;

-- Aggiungi foreign key per owner_user_id
ALTER TABLE review_entities
    ADD CONSTRAINT fk_review_owner FOREIGN KEY (owner_user_id) REFERENCES user_entities (id);

-- Crea indice per owner_user_id se utile per ricerche
CREATE INDEX idx_review_owner
    ON review_entities (owner_user_id);
