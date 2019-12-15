(ns dinsro.spec.actions.rates
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id ::ds/id)
(s/def :create-rates-request-valid/params (s/keys :req-un [::s.rates/rate ::currency-id ::s.rates/date]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-rates-request-valid/params]))

(s/def :create-rates-request/params (s/keys :opt-un [::s.rates/rate ::currency-id]))
(s/def ::create-handler-request (s/keys :req-un [:create-rates-request/params]))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (s/gen ::create-handler-request))
  )

(s/def :create-rates-response-valid/body (s/keys :req-un [::s.rates/item]))
(s/def :create-rates-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-rates-response-valid/body
                                                        :create-rates-response-valid/status]))

(s/def :create-rates-response-invalid-body/status #{:invalid})
(s/def :create-rates-response-invalid/body (s/keys :req-un [:create-rates-response-invalid-body/status]))
(s/def :create-rates-response-invalid/status #{status/bad-request})
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-rates-response-invalid/body
                                                          :create-rates-response-invalid/status]))

(s/def ::create-handler-response (s/or :invalid ::create-handler-response-invalid
                                       :valid   ::create-handler-response-valid))

(comment
  (gen/generate (s/gen ::create-handler-response-valid))
  (gen/generate (s/gen ::create-handler-response-invalid))
  (gen/generate (s/gen ::create-handler-response))
  )

(s/def :index-rates-response/items (s/coll-of ::s.rates/item))
(s/def :index-rates-response/body (s/keys :req-un [:index-rates-response/items]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-rates-response/body]))

(comment
  (gen/generate (s/gen :index-rates-response/items))
  (gen/generate (s/gen :index-rates-response/body))
  (gen/generate (s/gen ::index-handler-response))
  )

(s/def :read-rates-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-rates-request/path-params]))

(comment
  (gen/generate (s/gen :read-rates-request/path-params))
  (gen/generate (s/gen ::read-handler-request))
  )

(s/def :read-rates-response/body (s/keys :req-un [::s.rates/item]))
(s/def :read-rates-response-not-found-body/status #{:not-found})
(s/def :read-rates-response-not-found/body (s/keys :req-un [:read-rates-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-rates-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-rates-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(comment
  (gen/generate (s/gen :read-rates-response/body))
  (gen/generate (s/gen :read-rates-response/status))
  (gen/generate (s/gen :read-rates-response-not-found/body))
  (gen/generate (s/gen ::read-handler-response-valid))
  (gen/generate (s/gen ::read-handler-response-not-found))
  (gen/generate (s/gen ::read-handler-response))
  )

(s/def :delete-rates-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-rates-request/path-params (s/keys :req-un [:delete-rates-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-rates-request/path-params]))

(s/def ::delete-handler-response (s/keys))
