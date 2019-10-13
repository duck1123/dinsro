(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            dinsro.specs
            [taoensso.timbre :as timbre]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password-hash (hashers/derive password)}
             registration-data)
      nil)))

(defn create-user!
  [params]
  (if-let [user (prepare-user params)]
    (merge user (db/create-user! user))))
