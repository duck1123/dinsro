(ns dinsro.specs.actions.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs :as ds]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(s/def ::name string?)
(def name ::name)

(s/def ::url string?)
(def url ::url)

(s/def ::currency-id ::ds/id)
(def currency-id ::currency-id)

;; Create
(s/def ::create-params (s/keys :opt-un [::name ::url ::currency-id]))
(s/def ::create-params-valid (s/keys :req-un [::name ::url ::currency-id]))

(s/def :create-rate-sources-request-valid/params ::create-params-valid)
(s/def :create-rate-sources-request/params ::create-params)

(s/def ::create-request-valid (s/keys :req-un [:create-rate-sources-request-valid/params]))
(def create-request-valid ::create-request-valid)

(s/def ::create-request (s/keys :req-un [:create-rate-sources-request/params]))
(def create-request ::create-request)

(s/def :create-rate-sources-response-valid/body (s/keys :req-un [::m.rate-sources/item]))
(s/def :create-rate-sources-response-valid/status #{status/ok})
(s/def ::create-response-valid (s/keys :req-un [:create-rate-sources-response-valid/body
                                                :create-rate-sources-response-valid/status]))
(def create-response-valid ::create-response-valid)

(s/def ::create-response (s/or :invalid ::ds/common-response-invalid
                               :valid   ::create-response-valid))
(def create-response ::create-response)

(comment
  (ds/gen-key create-request-valid)
  (ds/gen-key create-response-valid))

;; Read


(s/def ::read-request (s/keys :req-un [:common-request-show/path-params]))
(def read-request ::read-request)

(s/def :read-rate-sources-response/body (s/keys :req-un [::m.rate-sources/item]))
(s/def ::read-response-valid (s/keys :req-un [:read-rate-sources-response/body]))
(s/def ::read-response (s/or :not-found ::ds/common-response-not-found
                             :valid     ::read-response-valid))
(def read-response ::read-response)

;; Delete

(s/def ::delete-request (s/keys :req-un [:common-request-show/path-params]))
(def delete-request ::delete-request)

(s/def ::delete-response (s/keys))
(def delete-response ::delete-response)

;; Index

(s/def ::index-request (s/keys))
(def index-request ::index-request)

(s/def :index-rate-sources-response/items (s/coll-of ::m.rate-sources/item))
(s/def :index-rate-sources-response/body (s/keys :req-un [:index-rate-sources-response/items]))
(s/def ::index-response (s/keys :req-un [:index-rate-sources-response/body]))
(def index-response ::index-response)

(comment

  (ds/gen-key index-request)
  (ds/gen-key index-response))
