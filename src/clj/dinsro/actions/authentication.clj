(ns dinsro.actions.authentication
  (:require [dinsro.actions.user.create-user :refer [create-user-response]]
            [ring.util.http-response :refer :all]))

(defn authenticate
  [authentication-data]
  (let [{:keys [email password]} authentication-data]
    (ok)))

(defn register
  "Register a user"
  [data]
  (create-user-response data)
  (ok))
