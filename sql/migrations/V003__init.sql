CREATE TABLE "user"
(
    id          SERIAL PRIMARY KEY NOT NULL,
    username    VARCHAR(255)       NOT NULL,
    password    VARCHAR(255)       NOT NULL,
    first_name  VARCHAR(255)       NOT NULL,
    last_name   VARCHAR(255)       NOT NULL,
    email       VARCHAR(255)       NOT NULL,
    is_admin    BOOLEAN                     DEFAULT false,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP                   DEFAULT NULL
);

CREATE TRIGGER user_trigger
    BEFORE UPDATE
    ON "user"
    FOR EACH ROW
EXECUTE PROCEDURE upd_timestamp();

CREATE UNIQUE INDEX api_credential_username_uindex ON "user" (username);
