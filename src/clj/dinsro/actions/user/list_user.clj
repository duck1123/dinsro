(ns dinsro.actions.user.list-user
  (:require [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn list-user-response
  [request]
  (let [users (db/list-users)]
    (ok {:users users})))
