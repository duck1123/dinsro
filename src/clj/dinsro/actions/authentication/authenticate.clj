(ns dinsro.actions.authentication.authenticate
  (:require [ring.util.http-response :refer :all]))

(defn authenticate
  [authentication-data]
  (let [{:keys [email password]} authentication-data]
    (ok)))
