(ns dinsro.specs.actions.admin-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

;; Create

(s/def ::create-params-valid
  (s/keys :req-un [::m.accounts/name
                   ::m.accounts/initial-value
                   ::m.accounts/user-id
                   ::m.accounts/currency-id]))
(def create-params-valid ::create-params-valid)

(s/def ::create-params
  (s/keys :opt-un [::m.accounts/name
                   ::m.accounts/initial-value
                   ::m.accounts/user-id
                   ::m.accounts/currency-id]))
(def create-params ::create-params)

(s/def :create-admin-account-valid/params ::create-params-valid)
(s/def ::create-request-valid (s/keys :req-un [:create-admin-account-valid/params]))

(s/def :create-admin-account/params ::create-params)
(s/def ::create-request (s/keys :req-un [:create-admin-account/params]))

(s/def ::create-response (s/keys))

;; Read

(s/def ::read-request (s/keys :req-un [:common-request-show/path-params]))
(def read-request ::read-request)

(s/def :read-admin-account-response-body/item ::m.accounts/item)
(s/def ::read-request-body (s/keys :req-un [:read-admin-account-response-body/item]))

(s/def :read-admin-account-response-success/body ::read-request-body)
(s/def ::read-response-success (s/keys :req-un [:read-admin-account-response-success/body]))

(s/def :read-admin-account-response-not-found-body/status ::ds/not-found-status)
(s/def ::read-response-not-found-body (s/keys :req-un [:read-admin-account-response-not-found-body/status]))

(s/def :read-account-response-not-found/body ::read-response-not-found-body)
(s/def ::read-response-not-found (s/keys :req-un [:read-account-response-not-found/body]))

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

(s/def :index-accounts-response/items (s/coll-of ::m.accounts/item))
(s/def :index-accounts-response/body (s/keys :req-un [:index-accounts-response/items]))
(s/def ::index-response (s/keys :req-un [:index-accounts-response/body]))
(def index-response ::index-response)

(comment

  (ds/gen-key index-request)
  (ds/gen-key index-response)
  )
