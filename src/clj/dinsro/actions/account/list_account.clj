(ns dinsro.actions.account.list-account
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn list-account-response []
  (let [accounts (db/list-accounts)]
    (ok accounts)))
