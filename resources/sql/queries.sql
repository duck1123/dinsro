-- :name create-account! :! :n
-- :doc creates a new account record
INSERT INTO accounts (name, user_id)
VALUES (:name, :user-id)

-- :name create-transaction! :! :n
-- :doc creates a new transaction record
INSERT INTO transactions (id, user_id)
VALUES (:id, :user-id)

-- :name create-user! :i!
-- :doc creates a new user record
INSERT INTO users
(name, email, password_hash)
VALUES (:name, :email, :password-hash)

-- :name delete-account! :! :n
-- :doc deletes an account record given the id
DELETE FROM accounts WHERE id = :id

-- :name delete-transaction! :! :n
-- :doc deletes a transaction record given the id
DELETE FROM transactions WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users WHERE id = :id

-- :name delete-users! :!
-- :doc delete all users
DELETE FROM users

-- :name find-user-by-email :? :1
-- :dock returns the user with a given email
SELECT * FROM users WHERE email = :email

-- :name read-account :? :1
-- :doc retrieves an account record given the id
SELECT * FROM accounts WHERE id = :id

-- :name read-transaction :? :1
-- :doc retrieves a transaction record given the id
SELECT * FROM transactions WHERE id = :id

-- :name read-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users WHERE id = :id

-- :name list-accounts :*
-- :doc retrieves all accounts
SELECT * FROM accounts

-- :name list-transactions :*
-- :doc retrieves all transactions
SELECT * FROM transactions

-- :name list-users :? :many
-- :doc retrieves all users
SELECT * FROM users
