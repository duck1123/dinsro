-- :name create-transaction! :! :n
-- :doc creates a new transaction record
INSERT INTO transactions
(id, user_id, created, updated)
VALUES (:id, :user_id, :created, :updated)

-- :name create-user! :i!
-- :doc creates a new user record
INSERT INTO users
(name, email, password_hash)
VALUES (:name, :email, :password_hash)

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

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
