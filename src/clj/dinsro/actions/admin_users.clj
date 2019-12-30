(ns dinsro.actions.admin-users
  (:require [dinsro.model.users :as m.user]
            [dinsro.spec.actions.users :as s.a.users]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params] :as request}]
  (or (try (if-let [user (m.user/create-record params)]
             (http/ok {:user user}))
           (catch Exception e nil))
      (http/bad-request {:status :invalid})))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (m.user/delete-record user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [request]
  (let [users (m.user/index-records)]
    (http/ok {:users users})))

;; Read

(defn-spec read-handler ::s.a.users/read-handler-response
  [request ::s.a.users/read-handler-request]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException e nil))]
      (if-let [user (m.user/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))
