CREATE TYPE statuses AS ENUM ('active', 'inactive', 'pending');

ALTER TABLE "user"

ADD COLUMN status           statuses;

UPDATE "user" SET status = 'active' WHERE status IS NULL;

ALTER TABLE "user" ALTER COLUMN status SET NOT NULL;
