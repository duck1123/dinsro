(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.user :as m.user]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-request/params (s/keys :opt [::s.users/name]))
(s/def ::create-request (s/keys :req-un [:create-request/params]))
(s/def :create-handler/status (constantly 200))
(s/def ::create-response (s/keys :req-un [:create-handler/status]))

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

(s/def :read-user-request/id string?)
(s/def :read-user-request/path-params (s/keys :req-un [:read-user-request/id]))
(s/def ::read-handler-request-valid (s/keys :req-un [:read-user-request/path-params]))
(s/def ::read-handler-request (s/keys :req-un [:read-user-request/path-params]))

(s/def :read-user-response/body (s/keys :req-un [::s.users/item]))
(s/def :read-user-response-not-found-body/status #{:not-found})
(s/def :read-user-response-not-found/body (s/keys :req-un [:read-user-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-user-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-user-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (let [{{id :id} :path-params} (timbre/spy :info request)]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException e nil))]
      (if-let [user (m.user/read-record (timbre/spy :info id))]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(comment
  ;; (gen/generate (s/gen :read-rates-request/path-params))
  (gen/generate (s/gen ::read-handler-request))
  (gen/generate (s/gen ::read-handler-request-valid))
  )
