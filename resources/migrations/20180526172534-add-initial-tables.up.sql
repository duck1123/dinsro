CREATE TABLE users
(id VARCHAR(20) PRIMARY KEY,
 name VARCHAR(30),
 email VARCHAR(30),
 password_hash VARCHAR(255),
 created TIMESTAMP,
 updated TIMESTAMP);

CREATE TABLE transactions
(
  id VARCHAR(20) PRIMARY KEY,
  user_id VARCHAR(20),
  created TIMESTAMP,
  updated TIMESTAMP
)
