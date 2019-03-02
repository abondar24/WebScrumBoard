CREATE TABLE IF NOT EXISTS contributor
(
  id         SERIAL NOT NULL PRIMARY KEY,
  user_id    BIGINT REFERENCES user (id),
  project_id BIGINT REFERENCES project (id),
  is_owner   BOOLEAN
);
