(ns dinsro.actions.account.read-account
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn read-account-response [accountId]
  (if-let [account (db/read-account {:id accountId})]
    (ok account)
    (status (ok) 404)))
