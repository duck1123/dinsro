(ns dinsro.model.account
  (:require [dinsro.db.core :as db]
            [taoensso.timbre :as timbre]))

(defn prepare-account
  [params]
  params)

(defn create-account!
  [params]
  (when-let [account (prepare-account params)]
    (timbre/info "Creating account" account)
    (merge account (db/create-account! account))))

(defn delete-account!
  [id]
  (db/delete-account! {:id id}))
