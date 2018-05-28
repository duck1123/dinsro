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
SELECT * FROM users
WHERE id = :id
