(ns dinsro.spec.actions.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.accounts :as s.accounts]
   [taoensso.timbre :as timbre]))

;; Create

(s/def ::create-params-valid-no-currency
  (s/keys :req-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id]))
(def create-params-valid-no-currency ::create-params-valid-no-currency)

(s/def ::create-params-valid
  (s/keys :req-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))
(def create-params-valid ::create-params-valid)

(s/def ::create-params
  (s/keys :opt-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))
(def create-params ::create-params)


(s/def :create-account-valid-no-currency/params ::create-params-valid-no-currency)
(s/def ::create-request-valid-no-currency (s/keys :req-un [:create-account-valid-no-currency/params]))


(s/def :create-account-valid/params ::create-params-valid)
(s/def ::create-request-valid (s/keys :req-un [:create-account-valid/params]))

(s/def :create-account/params ::create-params)
(s/def ::create-request (s/keys :req-un [:create-account/params]))
(def create-request ::create-request)

(s/def ::create-response (s/keys))
(def create-response ::create-response)

;; Read

(s/def ::read-request (s/keys :req-un [:common-request-show/path-params]))
(def read-request ::read-request)

(s/def :read-account-response-body/item ::s.accounts/item)
(s/def :read-account-response-success/body
  (s/keys :req-un [:read-account-response-body/item]))
(s/def ::read-response-success
  (s/keys :req-un [:read-account-response-success/body]))

(s/def :read-account-response-not-found-body/status #{:not-found})
(s/def :read-account-response-not-found/body
  (s/keys :req-un [:read-account-response-not-found-body/status]))
(s/def ::read-response-not-found
  (s/keys :req-un [:read-account-response-not-found/body]))

(s/def ::read-response
  (s/or :success   ::read-response-success
        :not-found ::read-response-not-found))
(def read-response ::read-response)

;; Delete

(s/def ::delete-request (s/keys :req-un [:common-request-show/path-params]))

(s/def ::delete-response-invalid (s/keys))
(s/def ::delete-response-success (s/keys))
(s/def ::delete-response (s/keys))

;; Index

(s/def ::index-request (s/keys))
(def index-request ::index-request)

(s/def :index-accounts-response/items (s/coll-of ::s.accounts/item))
(s/def :index-accounts-response/body (s/keys :req-un [:index-accounts-response/items]))
(s/def ::index-response (s/keys :req-un [:index-accounts-response/body]))
(def index-response ::index-response)
