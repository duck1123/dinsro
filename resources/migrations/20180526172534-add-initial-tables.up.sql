CREATE TABLE currencies (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  updated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
);

CREATE TABLE rates (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  currency_id bigint NOT NULL,
  value double NOT NULL,
  time TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  CONSTRAINT `rates_currency_fk` FOREIGN KEY (`currency_id`) REFERENCES `currencies` (`id`)
);

CREATE TABLE users (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  updated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  UNIQUE (email)
);

CREATE TABLE transactions (
  id VARCHAR(20) PRIMARY KEY,
  user_id VARCHAR(20),
  created TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  updated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  CONSTRAINT `transaction_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE accounts (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  user_id bigint NOT NULL,
  currency_id bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  updated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  CONSTRAINT `account_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `account_currency_fk` FOREIGN KEY (`currency_id`) REFERENCES `currencies` (`id`)
);
