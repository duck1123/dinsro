CREATE TABLE users (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  email VARCHAR(30) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  updated TIMESTAMP NOT NULL DEFAULT current_timestamp()
);
