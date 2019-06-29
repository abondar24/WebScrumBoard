create TABLE IF NOT EXISTS security_code
(
  id         SERIAL PRIMARY KEY,
  code       BIGINT NOT NULL,
  user_id    BIGINT REFERENCES wsuser (id) MATCH SIMPLE
    ON update NO ACTION
    ON delete NO ACTION,
  is_activated BOOLEAN
);
