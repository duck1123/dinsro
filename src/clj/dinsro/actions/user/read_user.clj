(ns dinsro.actions.user.read-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn read-user-response
  [userId]
  (if-let [user (db/read-user {:id userId})]
    (ok user)
    (status (ok) 404)))
