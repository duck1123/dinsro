(ns dinsro.spec.actions.accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

;; Create

(s/def ::create-params-valid
  (s/keys :req-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))

(s/def :create-account-valid/params ::create-params-valid)
(s/def :create-account/params
  (s/keys :opt-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-account-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-account/params]))

(s/def ::create-handler-response (s/keys))

;; Read

(s/def ::read-handler-request (s/keys :req-un [:common-request-show/path-params]))
(def read-handler-request ::read-handler-request)

(s/def :read-account-response-body/item ::s.accounts/item)
(s/def :read-account-response-success/body
  (s/keys :req-un [:read-account-response-body/item]))
(s/def ::read-handler-response-success
  (s/keys :req-un [:read-account-response-success/body]))

(s/def :read-account-response-not-found-body/status #{:not-found})
(s/def :read-account-response-not-found/body
  (s/keys :req-un [:read-account-response-not-found-body/status]))
(s/def ::read-handler-response-not-found
  (s/keys :req-un [:read-account-response-not-found/body]))

(s/def ::read-handler-response
  (s/or :success   ::read-handler-response-success
        :not-found ::read-handler-response-not-found))
(def read-handler-response ::read-handler-response)

;; Delete

(s/def ::delete-handler-request (s/keys :req-un [:common-request-show/path-params]))

(s/def ::delete-handler-response-invalid (s/keys))
(s/def ::delete-handler-response-success (s/keys))
(s/def ::delete-handler-response (s/keys))

;; Index

(s/def ::index-handler-request (s/keys))
(def index-handler-request ::index-handler-request)

(s/def :index-accounts-response/items (s/coll-of ::s.accounts/item))
(s/def :index-accounts-response/body (s/keys :req-un [:index-accounts-response/items]))
(s/def ::index-handler-response (s/keys :req-un [:index-accounts-response/body]))
(def index-handler-response ::index-handler-response)

(comment

  (ds/gen-key index-handler-request)
  (ds/gen-key index-handler-response)
  )
