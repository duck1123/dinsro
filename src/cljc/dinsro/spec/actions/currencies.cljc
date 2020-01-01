(ns dinsro.spec.actions.currencies
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [taoensso.timbre :as timbre]))

;; Create

(s/def :create-currency-request/params (s/keys :opt-un [::s.currencies/name]))
(s/def :create-currency-request-valid/params (s/keys :req-un [::s.currencies/name]))
(s/def :create-currency-request-valid/request (s/keys :req-un [:create-currency-request-valid/params]))
(s/def ::create-request-valid (s/keys :req-un [:create-currency-request-valid/params]))
(def create-request-valid ::create-request-valid)

(s/def ::create-request (s/keys :req-un [:create-currency-request/params]))
(def create-request ::create-request)

(s/def :create-currency-response/item ::s.currencies/item)
(s/def :create-currency-response/body (s/keys :req-un [:create-currency-response/item]))
(s/def ::create-response-valid (s/keys :req-un [:create-currency-response/body]))
(def create-response-valid ::create-response-valid)

(s/def ::create-response (s/or :valid   ::create-response-valid
                                       :invalid ::ds/common-response-invalid))
(def create-response ::create-response)

;; Read

(s/def ::read-request ::ds/common-read-request)
(def read-request ::read-request)

(s/def :read-currency-response-body/item ::s.currencies/item)
(s/def :read-currency-response-success/body
  (s/keys :req-un [:read-currency-response-body/item]))
(s/def ::read-response-success
  (s/keys :req-un [:read-currency-response-success/body]))
(def read-response-success ::read-response-success)

(s/def ::read-response
  (s/or :success   ::read-response-success
        :not-found ::ds/common-response-not-found))
(def read-response ::read-response)

;; Delete

(s/def ::delete-response (s/keys))
(def delete-response ::delete-response)

(s/def ::delete-request (s/keys))
(def delete-request ::delete-request)
