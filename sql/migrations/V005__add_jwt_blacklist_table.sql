CREATE TABLE "jwt_blacklist"
(
    id          SERIAL PRIMARY KEY NOT NULL,
    token       VARCHAR(255)       NOT NULL,
    create_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delete_time TIMESTAMP                   DEFAULT NULL
);

CREATE TRIGGER jwt_blacklist_trigger
    BEFORE UPDATE
    ON "jwt_blacklist"
    FOR EACH ROW
EXECUTE PROCEDURE upd_timestamp();
