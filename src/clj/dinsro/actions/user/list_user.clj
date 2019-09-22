(ns dinsro.actions.user.list-user
  (:require [clojure.data.json :as json]
            [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn list-user-response
  [request]
  (let [users (db/list-users)]
    (content-type (ok (json/json-str users)) "application/json")))
