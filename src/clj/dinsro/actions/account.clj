(ns dinsro.actions.account
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
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

(defn-spec prepare-record (s/nilable ::m.accounts/params)
  [params :create-handler/params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (when (s/valid? ::m.accounts/params params)
      params)))

(defn-spec create-handler ::create-handler-response
  [{:keys [params session]} ::create-handler-request]
  (or (let [user-id 1]
        (when-let [params (prepare-record params)]
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

(s/def :delete-handler-request-params/accountId (s/with-gen
                                                  string?
                                                  #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-handler-request/path-params (s/keys :req-un [:delete-handler-request-params/accountId]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-handler-request/path-params]))

(defn-spec delete-handler any?
  [{{:keys [accountId]} :path-params} ::delete-handler-request]
  (m.accounts/delete-account! (Integer/parseInt accountId))
  (http/ok {:status "ok"}))

(comment
  (prepare-record {:name "foo"})
  (prepare-record {})

  (gen/generate (gen/fmap str (s/gen pos-int?)))

  (gen/generate (s/gen :delete-handler-request-params/accountId))
  (gen/generate (s/gen ::delete-handler-request))

  (delete-handler {:path-params {:accountId "1"}})

  )
