ALTER TABLE sprint ADD COLUMN project_id  BIGINT REFERENCES project (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;