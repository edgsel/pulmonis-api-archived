CREATE TABLE "schooling"
(
    id          SERIAL PRIMARY KEY NOT NULL,
    title       VARCHAR(255)       NOT NULL,
    description VARCHAR(255)       NOT NULL,
    event_date  TIMESTAMP          NOT NULL,
    user_id     SERIAL             NOT NULL,
    free        BOOLEAN                     DEFAULT false,
    price       DOUBLE PRECISION,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP                   DEFAULT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id)
);

CREATE TRIGGER schooling_trigger
    BEFORE UPDATE
    ON "schooling"
    FOR EACH ROW
EXECUTE PROCEDURE upd_timestamp();
