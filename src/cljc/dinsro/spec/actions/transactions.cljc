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

(s/def ::create-params-valid (s/keys :req-un [::account-id ::currency-id ::date ::value]))
(def create-params-valid :create-transactions-request-valid/params)

(s/def :create-transactions-request-valid/params ::create-params-valid)

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

(s/def :create-transactions-response-valid/body (s/keys :req-un [::s.transactions/item]))
(s/def :create-transactions-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-transactions-response-valid/body
                                                        :create-transactions-response-valid/status]))
(def create-response-valid ::create-handler-response-valid)

(s/def ::create-handler-response (s/or :invalid ::ds/common-response-invalid
                                       :valid   ::create-handler-response-valid))
(def create-response ::create-handler-response)

;; Read

(s/def ::read-handler-request ::ds/common-read-request)

(s/def :read-transactions-response/body (s/keys :req-un [::s.transactions/item]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-transactions-response/body]))
(s/def ::read-handler-response (s/or :not-found ::ds/common-response-not-found
                                     :valid     ::read-handler-response-valid))

;; Delete

(s/def :delete-transactions-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-transactions-request/path-params (s/keys :req-un [:delete-transactions-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-transactions-request/path-params]))

(s/def ::delete-handler-response (s/keys))

;; Index

(s/def :index-transactions-response/items (s/coll-of ::s.transactions/item))
(s/def :index-transactions-response/body (s/keys :req-un [:index-transactions-response/items]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-transactions-response/body]))
