(ns dinsro.spec.actions.rate-sources
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(s/def ::url string?)
(s/def ::currency-id ::ds/id)


;; Create

(s/def :create-rate-sources-request-valid/params (s/keys :req-un [::name ::url ::currency-id]))
(s/def :create-rate-sources-request/params (s/keys :opt-un [::name ::url ::currency-id]))

(s/def ::create-handler-request-valid (s/keys :req-un [:create-rate-sources-request-valid/params]))
(def create-handler-request-valid ::create-handler-request-valid)

(s/def ::create-handler-request (s/keys :req-un [:create-rate-sources-request/params]))
(def create-handler-request ::create-handler-request)

(s/def :create-rate-sources-response-valid/body (s/keys :req-un [::s.rate-sources/item]))
(s/def :create-rate-sources-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-rate-sources-response-valid/body
                                                        :create-rate-sources-response-valid/status]))
(def create-handler-response-valid ::create-handler-response-valid)

(s/def :create-rate-sources-response-invalid-body/status #{:invalid})
(s/def :create-rate-sources-response-invalid/body (s/keys :req-un [:create-rate-sources-response-invalid-body/status]))
(s/def :create-rate-sources-response-invalid/status #{status/bad-request})
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-rate-sources-response-invalid/body
                                                          :create-rate-sources-response-invalid/status]))

(s/def ::create-handler-response (s/or :invalid ::create-handler-response-invalid
                                       :valid   ::create-handler-response-valid))
(def create-handler-response ::create-handler-response)

(comment
  (ds/gen-key create-handler-request-valid)
  (ds/gen-key create-handler-response-valid)
 )

;; Index

(s/def ::index-handler-request (s/keys))
(def index-handler-request ::index-handler-request)

(s/def :index-rate-sources-response/items (s/coll-of ::s.rate-sources/item))
(s/def :index-rate-sources-response/body (s/keys :req-un [:index-rate-sources-response/items]))
(s/def ::index-handler-response (s/keys :req-un [:index-rate-sources-response/body]))
(def index-handler-response ::index-handler-response)

(comment

  (ds/gen-key index-handler-request)
  (ds/gen-key index-handler-response)
  )


;; Read

(s/def :read-rate-sources-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-rate-sources-request/path-params]))

(s/def :read-rate-sources-response/body (s/keys :req-un [::s.rate-sources/item]))
(s/def :read-rate-sources-response-not-found-body/status ::ds/not-found-status)
(s/def :read-rate-sources-response-not-found/body (s/keys :req-un [:read-rate-sources-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-rate-sources-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-rate-sources-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

;; Delete

(s/def :delete-rate-sources-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-rate-sources-request/path-params (s/keys :req-un [:delete-rate-sources-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-rate-sources-request/path-params]))

(s/def ::delete-handler-response (s/keys))
