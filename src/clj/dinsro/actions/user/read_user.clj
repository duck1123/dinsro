(ns dinsro.actions.user.read-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn read-user-response
  [{{user-id :userId} :path-params}]
  (if-let [user (db/read-user {:id user-id})]
    (ok user)
    (status (ok) 404)))
