(ns dinsro.actions.user.delete-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn delete-user-response
  [request]
  (let [user-id (Integer/parseInt (:userId (:path-params request)))]
    (db/delete-user! {:id user-id})
    (ok {:id user-id})))
