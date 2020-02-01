(ns dinsro.actions.users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.users :as m.users]
   [dinsro.spec.actions.users :as s.a.users]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params]}]
  (or (try (if-let [user (m.users/create-record params)]
             (http/ok {:user user}))
           (catch Exception _ nil))
      (http/bad-request {:status :invalid})))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (m.users/delete-record user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [_]
  (let [users (m.users/index-records)]
    (http/ok {:users users})))

(s/fdef index-handler
  :args (s/cat :request (s/keys))
  :ret (s/keys))

;; Read

(defn read-handler
  [request ]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
      (if-let [user (m.users/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(s/fdef read-handler
  :args (s/cat :request ::s.a.users/read-request)
  :ret ::s.a.users/read-response)
