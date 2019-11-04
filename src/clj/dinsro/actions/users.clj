(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as m.user]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params] :as request}]
  (if-let [user (m.user/create-user! params)]
    (http/ok {:user user})
    (http/bad-request)))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:userId (:path-params request)))]
    (m.user/delete-user user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [request]
  (let [users (m.user/list-users)]
    (http/ok {:users users})))

(defn read-handler
  [request]
  (let [{{user-id :userId} :path-params} request]
    (if-let [user (try (m.user/read-user user-id) (catch Exception e nil))]
      (http/ok user)
      (http/not-found))))
