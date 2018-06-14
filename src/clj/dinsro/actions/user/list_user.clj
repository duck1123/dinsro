(ns dinsro.actions.user.list-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn list-user-response []
  (let [users (db/list-users)]
    (ok users)))
