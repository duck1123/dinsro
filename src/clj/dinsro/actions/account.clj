(ns dinsro.actions.account
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.account :as m.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-handler-valid/params (s/keys :req-un [::s.accounts/name]))
(s/def :create-handler/params (s/keys :opt-un [::s.accounts/name]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(s/def ::create-handler-response (s/keys))

(def param-rename-map
  {:name ::s.accounts/name})

(defn-spec prepare-record (s/nilable ::s.accounts/params)
  [params :create-handler/params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (when (s/valid? ::s.accounts/params params)
      params)))

(defn-spec create-handler ::create-handler-response
  [{:keys [params session]} ::create-handler-request]
  (or (let [user-id 1]
        (when-let [params (prepare-record params)]
          (let [item (m.accounts/create-record params #_(assoc params :user-id user-id))]
            (http/ok {:item item}))))
      (http/bad-request {:status :invalid})))

(defn index-handler
  [request]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(defn read-handler
  [{{:keys [accountId]} :path-params}]
  (if-let [account (m.accounts/read-record {:id accountId})]
    (http/ok account)
    (http/not-found {})))

(s/def :delete-handler-request-params/accountId (s/with-gen
                                                  string?
                                                  #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-handler-request/path-params (s/keys :req-un [:delete-handler-request-params/accountId]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-handler-request/path-params]))

(defn-spec delete-handler any?
  [{{:keys [accountId]} :path-params} ::delete-handler-request]
  (try
    (let [id (Integer/parseInt accountId)]
      (m.accounts/delete-record id))
    (http/ok {:status "ok"})
    (catch NumberFormatException e
      (http/bad-request {:input :invalid}))))

(comment
  (prepare-record {:name "foo"})

  (gen/generate (gen/fmap str (s/gen pos-int?)))
  (gen/generate (s/gen :delete-handler-request-params/accountId))
  (gen/generate (s/gen ::delete-handler-request))

  (delete-handler {:path-params {:accountId "s"}})

  )
