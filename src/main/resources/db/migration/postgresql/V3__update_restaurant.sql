-- V3__update_restaurant_entity_add_address_and_characteristics.sql

-- Rimuovi le vecchie colonne latitude e longitude
ALTER TABLE restaurant_entities
DROP COLUMN IF EXISTS latitude,
    DROP COLUMN IF EXISTS longitude;

-- Aggiungi la colonna 'address' di tipo jsonb
ALTER TABLE restaurant_entities
    ADD COLUMN address jsonb;

-- Aggiungi la colonna 'characteristics' di tipo jsonb
ALTER TABLE restaurant_entities
    ADD COLUMN characteristics jsonb;

-- Crea indice GIN sulla colonna 'characteristics' per query efficienti
CREATE INDEX idx_restaurant_characteristics
    ON restaurant_entities
    USING GIN (characteristics);
