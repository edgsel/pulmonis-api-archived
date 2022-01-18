ALTER TABLE "schooling"

ADD COLUMN address                  VARCHAR(255),
ADD COLUMN city                     VARCHAR(100),
ADD COLUMN category                 VARCHAR(100),
ADD COLUMN published                 BOOLEAN      NOT NULL DEFAULT FALSE,
ADD COLUMN registration_deadline    TIMESTAMP;

UPDATE "schooling" SET address = 'Default 4';
UPDATE "schooling" SET city = 'Default';
UPDATE "schooling" SET category = 'Category';
UPDATE "schooling" SET registration_deadline = '2015-12-03T10:15:35';


ALTER TABLE "schooling" ALTER COLUMN address                SET NOT NULL;
ALTER TABLE "schooling" ALTER COLUMN city                   SET NOT NULL;
ALTER TABLE "schooling" ALTER COLUMN category               SET NOT NULL;
ALTER TABLE "schooling" ALTER COLUMN registration_deadline  SET NOT NULL;
