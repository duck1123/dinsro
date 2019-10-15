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
    (merge account {:id (db/create-account! account)})))
