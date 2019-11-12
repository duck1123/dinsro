(ns dinsro.actions.account
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [dinsro.model.account :as m.accounts]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-handler-valid/params (s/keys :req-un [::m.accounts/name]))
(s/def :create-handler/params (s/keys :opt-un [::m.accounts/name]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(s/def ::create-handler-response (s/keys))

(def param-rename-map
  {:name ::m.accounts/name})

(defn-spec prepare-record ::m.accounts/params
  [params :create-handler/params]
  (-> params
      (set/rename-keys param-rename-map)
      (select-keys (vals param-rename-map))))

(defn-spec create-handler ::create-handler-response
  [{:keys [params session]} ::create-handler-request]
  (or (let [user-id 1
            params (prepare-record params)]
        (when (s/valid? ::m.accounts/params params)
          (let [item (m.accounts/create-account! params #_(assoc params :user-id user-id))]
            (http/ok {:item item}))))
      (http/bad-request {:status :invalid})))

(defn index-handler
  [request]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(defn read-handler
  [{{:keys [accountId]} :path-params}]
  (if-let [account (m.accounts/read-account {:id accountId})]
    (http/ok account)
    (http/not-found {})))

(defn delete-handler
  [{{:keys [accountId]} :path-params}]
  (m.accounts/delete-account! accountId)
  (http/ok {:status "ok"}))
