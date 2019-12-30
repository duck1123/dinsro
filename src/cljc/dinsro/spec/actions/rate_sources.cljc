(ns dinsro.spec.actions.rate-sources
  (:refer-clojure :exclude [name])
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec :as ds]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(def name ::name)

(s/def ::url string?)
(def url ::url)

(s/def ::currency-id ::ds/id)
(def currency-id ::currency-id)

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

(s/def ::create-handler-response (s/or :invalid ::ds/common-response-invalid
                                       :valid   ::create-handler-response-valid))
(def create-handler-response ::create-handler-response)

(comment
  (ds/gen-key create-handler-request-valid)
  (ds/gen-key create-handler-response-valid)
 )

;; Read

(s/def ::read-handler-request (s/keys :req-un [:common-request-show/path-params]))
(def read-handler-request ::read-handler-request)

(s/def :read-rate-sources-response/body (s/keys :req-un [::s.rate-sources/item]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-rate-sources-response/body]))
(s/def ::read-handler-response (s/or :not-found ::ds/common-response-not-found
                                     :valid     ::read-handler-response-valid))
(def read-handler-response ::read-handler-response)

;; Delete

(s/def ::delete-handler-request (s/keys :req-un [:common-request-show/path-params]))
(def delete-handler-request ::delete-handler-request)

(s/def ::delete-handler-response (s/keys))
(def delete-handler-response ::delete-handler-response)

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
