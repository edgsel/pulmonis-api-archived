ALTER TABLE "news"

ADD COLUMN published_et         BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN published_en         BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN published_ru         BOOLEAN NOT NULL DEFAULT FALSE
