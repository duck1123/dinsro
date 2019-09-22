(ns dinsro.actions.user.read-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn read-user-response
  [request]
  (let [{{user-id :userId} :path-params} request]
    (if-let [user (db/read-user {:id user-id})]
      (ok user)
      (status (ok) 404))))
