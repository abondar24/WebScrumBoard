CREATE TABLE IF NOT EXISTS sprint
(
  id         SERIAL PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  start_date TIMESTAMP    NOT NULL,
  end_date   TIMESTAMP    NOT NULL
);