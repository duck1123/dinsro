(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.user :as m.user]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
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

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException e nil))]
      (if-let [user (m.user/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))
