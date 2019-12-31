(ns dinsro.spec.actions.accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

;; Create

(s/def :create-account-valid/params
  (s/keys :req-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))
(s/def :create-account/params
  (s/keys :opt-un [::s.accounts/name
                   ::s.accounts/initial-value
                   ::s.accounts/user-id
                   ::s.accounts/currency-id]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-account-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-account/params]))
(s/def ::create-handler-response (s/keys))

;; Read

;; - request

(s/def :read-account-request-path-params/id ::ds/id-string)
(s/def :read-account-request/path-params (s/keys :req-un [:read-account-request-path-params/id]))
(s/def ::read-handler-request (s/keys :req-un [:read-account-request/path-params]))

;; - response

(s/def :read-account-response-body/item ::s.accounts/item)
(s/def :read-account-response-success/body
  (s/keys :req-un [:read-account-response-body/item]))
(s/def ::read-handler-response-success
  (s/keys :req-un [:read-account-response-success/body]))

(s/def :read-account-response-not-found-body/status keyword?)
(s/def :read-account-response-not-found/body
  (s/keys :req-un [:read-account-response-not-found-body/status]))
(s/def ::read-handler-response-not-found
  (s/keys :req-un [:read-account-response-not-found/body]))

(s/def ::read-handler-response
  (s/or :success   ::read-handler-response-success
        :not-found ::read-handler-response-not-found))

;; Delete

(s/def :delete-account-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-account-request/path-params (s/keys :req-un [:delete-account-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-account-request/path-params]))

(s/def ::delete-handler-response-invalid (s/keys))
(s/def ::delete-handler-response-success (s/keys))
(s/def ::delete-handler-response (s/keys))
