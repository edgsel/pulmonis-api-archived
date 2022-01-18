ALTER TABLE "news"

ADD COLUMN category VARCHAR(100);

UPDATE "news" SET category = '';

ALTER TABLE "news" ALTER COLUMN category SET NOT NULL;
