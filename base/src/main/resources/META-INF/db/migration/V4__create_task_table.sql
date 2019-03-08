CREATE TABLE IF NOT EXISTS task
(
  id            SERIAL PRIMARY KEY,
  contributor_id BIGINT REFERENCES contributor (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
  task_state     VARCHAR(255) NOT NULL,
  story_points   INT,
  start_date     TIMESTAMP,
  end_date       TIMESTAMP
);