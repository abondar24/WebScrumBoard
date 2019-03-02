CREATE TABLE IF NOT EXISTS task
(
  id             SERIAL NOT NULL PRIMARY KEY,
  contributor_id BIGINT REFERENCES contributor (id),
  task_state     VARCHAR(255) NOT NULL,
  story_points   INT,
  start_date     TIMESTAMP,
  end_date       TIMESTAMP
);