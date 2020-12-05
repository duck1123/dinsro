(ns dinsro.actions.admin-users
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.queries.users :as q.users]
   [dinsro.specs.actions.admin-users :as s.a.admin-users]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params]}]
  (or (try (if-let [user (q.users/create-record params)]
             (http/ok {:user user}))
           (catch Exception _ nil))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.admin-users/read-request => ::s.a.admin-users/read-response]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
      (if-let [user (q.users/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (q.users/delete-record user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [_]
  (let [users (q.users/index-records)]
    (http/ok {:users users})))
