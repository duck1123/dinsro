(ns dinsro.actions.accounts
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :as http]))

(defn list-account-response []
  (let [accounts (db/list-accounts)]
    (http/ok accounts)))

(defn read-account-response [accountId]
  (if-let [account (db/read-account {:id accountId})]
    (http/ok account)
    (http/status (http/ok) 404)))
