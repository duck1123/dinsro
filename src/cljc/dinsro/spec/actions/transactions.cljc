(ns dinsro.spec.actions.transactions
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec :as ds]
            [dinsro.spec.transactions :as s.transactions]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::account-id :db/id)
(def account-id ::account-id)

(s/def ::date ds/date-string)
(def date ::date)

(s/def ::value ::ds/json-doubleable)
(def value ::value)

(s/def ::description string?)
(def description ::description)

;; Create

(s/def ::params-valid (s/keys :req-un [::account-id ::date ::description ::value]))

(s/def ::create-params-valid ::params-valid)
(def create-params-valid ::create-params-valid)

(s/def :create-transactions-request-valid/params ::create-params-valid)

(s/def ::create-request-valid
  (s/keys :req-un [:create-transactions-request-valid/params]))
(def create-request-valid ::create-request-valid)

(s/def ::create-params (s/keys :opt-un [::account-id ::date ::description ::value]))
(def create-params ::create-params)

(s/def :create-transactions-request/params ::create-params)
(s/def ::create-request (s/keys :req-un [:create-transactions-request/params]))
(def create-request ::create-request)

(s/def :create-transactions-response-valid/body (s/keys :req-un [::s.transactions/item]))
(s/def :create-transactions-response-valid/status #{status/ok})
(s/def ::create-response-valid (s/keys :req-un [:create-transactions-response-valid/body
                                                :create-transactions-response-valid/status]))
(def create-response-valid ::create-response-valid)

(s/def ::create-response (s/or :invalid ::ds/common-response-invalid
                               :valid   ::create-response-valid))
(def create-response ::create-response)

;; Read

(s/def ::read-request ::ds/common-read-request)

(s/def :read-transactions-response/body (s/keys :req-un [::s.transactions/item]))
(s/def ::read-response-valid (s/keys :req-un [:read-transactions-response/body]))
(def read-response-valid ::read-response-valid)

(s/def ::read-response (s/or :not-found ::ds/common-response-not-found
                             :valid     ::read-response-valid))
(def read-response ::read-response)

;; Delete

(s/def :delete-transactions-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-transactions-request/path-params (s/keys :req-un [:delete-transactions-request-params/id]))
(s/def ::delete-request (s/keys :req-un [:delete-transactions-request/path-params]))

(s/def ::delete-response (s/keys))

;; Index

(s/def :index-transactions-response/items (s/coll-of ::s.transactions/item))
(s/def :index-transactions-response/body (s/keys :req-un [:index-transactions-response/items]))
(s/def ::index-request (s/keys))
(s/def ::index-response (s/keys :req-un [:index-transactions-response/body]))
