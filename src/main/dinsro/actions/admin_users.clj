(ns dinsro.actions.admin-users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.users :as m.users]
   [dinsro.specs.actions.admin-users :as s.a.admin-users]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

;; Create

(defn create-handler
  [{:keys [params]}]
  (or (try (if-let [user (m.users/create-record params)]
             (http/ok {:user user}))
           (catch Exception _ nil))
      (http/bad-request {:status :invalid})))

;; Read

(defn read-handler
  [request]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
      (if-let [user (m.users/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-users/read-request)
  :ret ::s.a.admin-users/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (m.users/delete-record user-id)
    (http/ok {:id user-id})))

;; Index

(defn index-handler
  [_]
  (let [users (m.users/index-records)]
    (http/ok {:users users})))
