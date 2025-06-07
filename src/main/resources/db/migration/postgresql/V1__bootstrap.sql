CREATE TABLE restaurant_entities
(
    id              UUID NOT NULL,
    name            VARCHAR(511),
    description     VARCHAR(1027),
    latitude        DOUBLE PRECISION CHECK (latitude BETWEEN -90 AND 90),
    longitude       DOUBLE PRECISION CHECK (longitude BETWEEN -180 AND 180),
    thumbnail       VARCHAR(1027),
    owner_user_id   UUID NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE,
    version         BIGINT,
    CONSTRAINT pk_restaurant_entities PRIMARY KEY (id)
);

CREATE TABLE review_entities
(
    id              UUID NOT NULL,
    restaurant_id   UUID NOT NULL,
    user_id         UUID NOT NULL,
    content         TEXT,
    rating          INTEGER CHECK (rating BETWEEN 1 AND 5),
    created_at      TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE,
    version         BIGINT,
    CONSTRAINT pk_review_entities PRIMARY KEY (id)
);

CREATE TABLE vote_entities
(
    id              UUID NOT NULL,
    review_id       UUID NOT NULL,
    user_id         UUID NOT NULL,
    vote_type       VARCHAR(10) CHECK (vote_type IN ('UP', 'DOWN')),
    created_at      TIMESTAMP WITH TIME ZONE,
    version         BIGINT,
    CONSTRAINT pk_vote_entities PRIMARY KEY (id)
);

CREATE TABLE user_entities
(
    id               UUID NOT NULL,
    name             VARCHAR(511),
    surname          VARCHAR(511),
    email            VARCHAR(511),
    role             VARCHAR(127),
    password_hash    VARCHAR(255) NOT NULL,
    disabled_at      TIMESTAMP WITH TIME ZONE,
    created_at       TIMESTAMP WITH TIME ZONE,
    updated_at       TIMESTAMP WITH TIME ZONE,
    version          BIGINT,
    CONSTRAINT pk_user_entities PRIMARY KEY (id)
);


CREATE UNIQUE INDEX idx_userentities_email ON user_entities (email);

CREATE INDEX idx_restaurant_owner ON restaurant_entities (owner_user_id);
CREATE INDEX idx_review_restaurant ON review_entities (restaurant_id);
CREATE INDEX idx_review_user ON review_entities (user_id);
CREATE INDEX idx_vote_review ON vote_entities (review_id);
CREATE INDEX idx_vote_user ON vote_entities (user_id);

-- Foreign Key constraints

ALTER TABLE restaurant_entities
    ADD CONSTRAINT fk_restaurant_owner FOREIGN KEY (owner_user_id) REFERENCES user_entities (id);

ALTER TABLE review_entities
    ADD CONSTRAINT fk_review_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant_entities (id) ON DELETE CASCADE;

ALTER TABLE review_entities
    ADD CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user_entities (id);

ALTER TABLE vote_entities
    ADD CONSTRAINT fk_vote_review FOREIGN KEY (review_id) REFERENCES review_entities (id) ON DELETE CASCADE;

ALTER TABLE vote_entities
    ADD CONSTRAINT fk_vote_user FOREIGN KEY (user_id) REFERENCES user_entities (id);

-- Evitare voti multipli per review e user
CREATE UNIQUE INDEX uq_vote_user_review ON vote_entities (review_id, user_id);
