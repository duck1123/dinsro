-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, name, email, password_hash, created, updated)
VALUES (:id, :name, :email, :password_hash, :created, :updated)

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name create-transaction! :! :n
-- :doc creates a new transaction record
INSERT INTO transactions
(id, user_id, created, updated)
VALUES (:id, :user_id, :created, :updated)
