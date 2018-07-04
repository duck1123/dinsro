(ns dinsro.actions.authentication
  (:require [ring.util.http-response :refer :all]))

(defn authenticate
  [authentication-data]
  (let [{:keys [email password]} authentication-data]
    (ok)))

(defn register
  [data
   ;; :- :dinsro.specs/register-request
   ]
  (ok))
