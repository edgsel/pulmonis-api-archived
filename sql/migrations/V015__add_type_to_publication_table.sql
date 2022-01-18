CREATE TYPE types AS ENUM ('news', 'article');

ALTER TABLE "publication"

ADD COLUMN content_type       types;

UPDATE "publication" SET content_type = 'news' WHERE content_type IS NULL;

ALTER TABLE "publication" ALTER COLUMN content_type SET NOT NULL;
