CREATE TABLE "news"
(
    id              SERIAL PRIMARY KEY NOT NULL,
    title_ee        VARCHAR(255),
    title_ru        VARCHAR(255),
    title_en        VARCHAR(255),
    description_ee  VARCHAR(255),
    description_ru  VARCHAR(255),
    description_en  VARCHAR(255),
    body_ee         VARCHAR,
    body_ru         VARCHAR,
    body_en         VARCHAR,
    language_ee     BOOLEAN            NOT NULL DEFAULT false,
    language_en     BOOLEAN            NOT NULL DEFAULT false,
    language_ru     BOOLEAN            NOT NULL DEFAULT false,
    user_id         SERIAL             NOT NULL,
    published       BOOLEAN            NOT NULL DEFAULT false,
    create_time     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delete_time     TIMESTAMP                   DEFAULT NULL,
    CONSTRAINT fk_user_news
        FOREIGN KEY (user_id)
            REFERENCES "user" (id)
);

CREATE TRIGGER news_trigger
    BEFORE UPDATE
    ON "news"
    FOR EACH ROW
EXECUTE PROCEDURE upd_timestamp();
