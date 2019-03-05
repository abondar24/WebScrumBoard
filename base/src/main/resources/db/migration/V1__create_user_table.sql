CREATE TABLE IF NOT EXISTS user
(
  id         BIGINT       NOT NULL PRIMARY KEY,
  login      VARCHAR(255) NOT NULL,
  email      VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  password   VARCHAR(255) NOT NULL,
  roles      VARCHAR(255) NOT NULL
);