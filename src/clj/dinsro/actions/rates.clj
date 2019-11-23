(ns dinsro.actions.rates
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-rates-valid/params (s/keys :req-un [::s.rates/value]))
(comment
  (gen/generate (s/gen :create-rates-valid/params))
  )

(s/def :create-rates/params (s/keys :opt-un [::s.rates/name]))
(comment
  (gen/generate (s/gen :create-rates/params))
  )

(s/def :create-rates-response/items (s/coll-of ::s.rates/item))
(s/def :create-rates-response/body (s/keys :req-un [:create-rates-response/items]))

(s/def :read-rates-response/body (s/keys))
(comment
  (gen/generate (s/gen :read-rates-response/body))
  )

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

;; Index

(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys))

(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.rates/index-records)]
    (http/ok {:model :rates :items items})))

;; Create

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
          (when-let [item (m.rates/create-record params)]
            (http/ok {:item item}))))
      (http/bad-request {:status :invalid})))

;; Read

(s/def ::read-handler-request (s/keys))
(comment
  (gen/generate (s/gen ::read-handler-request))
  )

(s/def ::read-handler-response (s/keys :req-un [:read-rates-response/body]))
(comment
  (gen/generate (s/gen ::read-handler-response))
  )

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (http/ok {:status "ok"}))
