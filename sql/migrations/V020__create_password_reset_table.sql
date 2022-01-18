CREATE TABLE "password_reset_token"
(
    id          SERIAL PRIMARY KEY          NOT NULL,
    token       VARCHAR(60)                 DEFAULT NULL,
    user_id     SERIAL                      NOT NULL,
    expiry_time TIMESTAMP                   DEFAULT NULL,
    create_time TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP                   DEFAULT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TRIGGER password_reset_token_trigger
    BEFORE UPDATE
    ON "password_reset_token"
    FOR EACH ROW
EXECUTE PROCEDURE upd_timestamp();
