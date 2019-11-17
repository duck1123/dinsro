(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.user :as m.user]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-request/params (s/keys :opt [::m.user/name]))
(s/def ::create-request (s/keys :req-un [:create-request/params]))
(s/def :create-handler/status (constantly 200))
(s/def ::create-response (s/keys :req-un [:create-handler/status]))

(defn create-handler
  [{:keys [params] :as request}]
  (or (try (if-let [user (m.user/create-user! params)]
             (http/ok {:user user}))
           (catch Exception e nil))
      (http/bad-request {:status :invalid})))

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
  (let [{{id :userId} :path-params} request]
    (if-let [user (try (m.user/read-record id) (catch Exception e nil))]
      (http/ok user)
      (http/not-found))))
