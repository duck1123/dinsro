(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            dinsro.specs
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [registration-data] :as request}]
  (if (model.user/create-user! registration-data)
    (ok "ok")))

(defn delete-handler
  [request]
  (let [user-id (timbre/spy :info (Integer/parseInt (:userId (:path-params request))))]
    (db/delete-user! {:id user-id})
    (ok {:id user-id})))

(defn index-handler
  [request]
  (let [users (db/list-users)]
    (ok {:users users})))

(defn read-handler
  [request]
  (let [{{user-id :userId} :path-params} request]
    (if-let [user (db/read-user {:id user-id})]
      (ok user)
      (status (ok) 404))))
