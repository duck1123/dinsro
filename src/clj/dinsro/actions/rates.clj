(ns dinsro.actions.rates
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

;; Create

(s/def :create-rates-valid/params (s/keys :req-un [::s.rates/value]))
(comment
  (gen/generate (s/gen :create-rates-valid/params))
  )

(s/def :create-rates/params (s/keys :opt-un [::s.rates/name]))
(comment
  (gen/generate (s/gen :create-rates/params))
  )

(s/def :create-rates-response/items (s/coll-of ::s.rates/item))

(s/def :create-rates-response/item ::s.rates/item)

(s/def :create-rates-response/body (s/keys :req-un [:create-rates-response/item]))

(s/def :create-rates-valid/request (s/keys :req-un [:create-rates-valid/params]))
(comment
  (gen/generate (s/gen :create-rates-valid/request))
  )

(def param-rename-map
  {:value ::s.rates/value})

(defn-spec prepare-record (s/nilable ::s.rates/params)
  [params :create-rates/params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (when (s/valid? ::s.rates/params params)
      params)))

(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(comment
  (gen/generate (s/gen ::create-handler-request))
  )

(s/def ::create-handler-response (s/keys :req-un [:create-rates-response/body]))
(comment
  (gen/generate (s/gen ::create-handler-response))
  )

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (timbre/spy :info (prepare-record params))]
          (when-let [id (m.rates/create-record params)]
            (http/ok {:item (m.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(s/def :index-rates-response/items (s/coll-of ::s.rates/item))
(comment
  (gen/generate (s/gen :index-rates-response/items))
  )

(s/def :index-rates-response/body (s/keys :req-un [:index-rates-response/items]))
(comment
  (gen/generate (s/gen :index-rates-response/body))
  )

(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-rates-response/body]))
(comment
  (gen/generate (s/gen ::index-handler-response))
  )

(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.rates/index-records)]
    (http/ok {:model :rates :items items})))

;; Read

(s/def :read-rates-response/body (s/keys :req-un [::s.rates/item]))
(comment
  (gen/generate (s/gen :read-rates-response/body))
  )

(s/def :read-rates-request/path-params (s/keys :req-un [:db/id]))
(comment
  (gen/generate (s/gen :read-rates-request/path-params))
  )

(s/def :read-rates-response/status keyword?)
(comment
  (gen/generate (s/gen :read-rates-response/status))
  )

(s/def ::read-handler-request (s/keys :req-un [:read-rates-request/path-params]))
(comment
  (gen/generate (s/gen ::read-handler-request))
  )

(s/def ::read-handler-response-valid (s/keys :req-un [:read-rates-response/body]))
(comment
  (gen/generate (s/gen ::read-handler-response-valid))
  )

(s/def ::read-handler-response-not-found (s/keys :req-un [:read-rates-response/status]))
(comment
  (gen/generate (s/gen ::read-handler-response-not-found))
  )

(s/def ::read-handler-response (s/or ::read-handler-response-valid ::read-handler-response-not-found))
(comment
  (gen/generate (s/gen ::read-handler-response))
  )

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (or (let [params (:path-params (timbre/spy :info request))]
        (let [id (:id params)]
          (let [item (m.rates/read-record id)]
            (http/ok {:item (timbre/spy :info item)}))))
      (http/not-found {:status :not-found})))
