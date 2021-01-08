CREATE TABLE IF NOT EXISTS security_code
(
  id         SERIAL PRIMARY KEY,
  code       BIGINT NOT NULL,
  user_id    BIGINT REFERENCES wsuser (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
  is_activated BOOLEAN
);
