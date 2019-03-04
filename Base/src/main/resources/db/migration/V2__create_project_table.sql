CREATE TABLE IF NOT EXISTS project
(
  id         BIGINT       NOT NULL PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  start_date TIMESTAMP,
  end_date   TIMESTAMP,
  repository VARCHAR(255),
  is_active  BOOLEAN
);
