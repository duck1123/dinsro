(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            dinsro.specs
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn create
  [{:keys [registration-data] :as request}]
  {:pre [(s/valid? :dinsro.specs/register-request registration-data)]}
  (if (model.user/create-user! registration-data)
    (ok "ok")))

(s/fdef create-user-response
  :args (s/cat :data :dinsro.specs/register-request))

(defn delete
  [request]
  (let [user-id (timbre/spy :info (Integer/parseInt (:userId (:path-params request))))]
    (db/delete-user! {:id user-id})
    (ok {:id user-id})))

(defn index
  [request]
  (let [users (db/list-users)]
    (ok {:users users})))

(defn read
  [request]
  (let [{{user-id :userId} :path-params} request]
    (if-let [user (db/read-user {:id user-id})]
      (ok user)
      (status (ok) 404))))
