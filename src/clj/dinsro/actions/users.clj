(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as m.user]
            [dinsro.specs :as ds]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [registration-data] :as request}]
  (if (m.user/create-user! registration-data)
    (ok "ok")))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:userId (:path-params request)))]
    (m.user/delete-user user-id)
    (ok {:id user-id})))

(defn index-handler
  [request]
  (let [users (m.user/list-users)]
    (ok {:users users})))

(defn read-handler
  [request]
  (let [{{user-id :userId} :path-params} request]
    (if-let [user (m.user/read-user user-id)]
      (ok user)
      (status (ok) 404))))
