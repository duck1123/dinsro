(ns dinsro.spec.actions.transactions
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id :db/id)
(def currency-id ::currency-id)

(s/def ::account-id :db/id)
(def account-id ::account-id)

(s/def ::date ds/date-string)
(def date ::date)

(s/def ::value ::ds/json-doubleable)
(def value ::value)

;; Create

;; - request

(s/def ::create-params-valid (s/keys :req-un [::account-id ::currency-id ::date ::value]))

(s/def :create-transactions-request-valid/params ::create-params-valid)
(def create-params-valid :create-transactions-request-valid/params)

(s/def ::create-handler-request-valid
  (s/keys :req-un [:create-transactions-request-valid/params]))
(def create-request-valid ::create-handler-request-valid)

(s/def :create-transactions-request/params
  (s/keys :opt-un [::account-id ::currency-id ::date ::value]))
(def create-params :create-transactions-request/params)

(s/def ::create-handler-request
  (s/keys :req-un [:create-transactions-request/params]))
(def create-request ::create-handler-request)

(comment
  (ds/gen-key create-request-valid)
  (ds/gen-key create-request)
  )

;; - response

(s/def :create-transactions-response-valid/body (s/keys :req-un [::s.transactions/item]))
(s/def :create-transactions-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-transactions-response-valid/body
                                                        :create-transactions-response-valid/status]))
(def create-response-valid ::create-handler-response-valid)

(s/def :create-transactions-response-invalid-body/status #{:invalid})
(s/def :create-transactions-response-invalid/body (s/keys :req-un [:create-transactions-response-invalid-body/status]))
(s/def :create-transactions-response-invalid/status #{status/bad-request})
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-transactions-response-invalid/body
                                                          :create-transactions-response-invalid/status]))
(def create-response-bad-request ::create-handler-response-invalid)

(s/def ::create-handler-response (s/or :invalid ::create-handler-response-invalid
                                       :valid   ::create-handler-response-valid))
(def create-response ::create-handler-response)

;; Index

(s/def :index-transactions-response/items (s/coll-of ::s.transactions/item))
(s/def :index-transactions-response/body (s/keys :req-un [:index-transactions-response/items]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-transactions-response/body]))

(s/def :read-transactions-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-transactions-request/path-params]))

(s/def :read-transactions-response/body (s/keys :req-un [::s.transactions/item]))
(s/def :read-transactions-response-not-found-body/status #{:not-found})
(s/def :read-transactions-response-not-found/body (s/keys :req-un [:read-transactions-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-transactions-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-transactions-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(s/def :delete-transactions-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-transactions-request/path-params (s/keys :req-un [:delete-transactions-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-transactions-request/path-params]))

(s/def ::delete-handler-response (s/keys))
