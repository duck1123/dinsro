(ns dinsro.spec.actions.currencies
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.currencies :as s.currencies]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

;; Create

(s/def :create-currency-request/params (s/keys :opt-un [::s.currencies/name]))
(s/def :create-currency-request-valid/params (s/keys :req-un [::s.currencies/name]))
(s/def :create-currency-request-valid/request (s/keys :req-un [:create-currency-request-valid/params]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-currency-request-valid/params]))
(def create-handler-request-valid ::create-handler-request-valid)

(s/def ::create-handler-request (s/keys :req-un [:create-currency-request/params]))
(def create-handler-request ::create-handler-request)

(s/def :create-currency-response/item ::s.currencies/item)
(s/def :create-currency-response/body (s/keys :req-un [:create-currency-response/item]))
(s/def ::create-handler-response-valid (s/keys :req-un [:create-currency-response/body]))
(def create-handler-response-valid ::create-handler-response-valid)

(s/def ::create-handler-response (s/or :valid   ::create-handler-response-valid
                                       :invalid ::ds/common-response-invalid))
(def create-handler-response ::create-handler-response)

;; Read

(s/def ::read-handler-request ::ds/common-read-request)
(def read-handler-request ::read-handler-request)

(s/def :read-currency-response-body/item ::s.currencies/item)
(s/def :read-currency-response-success/body
  (s/keys :req-un [:read-currency-response-body/item]))
(s/def ::read-handler-response-success
  (s/keys :req-un [:read-currency-response-success/body]))
(def read-handler-response-success ::read-handler-response-success)

(s/def ::read-handler-response
  (s/or :success   ::read-handler-response-success
        :not-found ::ds/common-response-not-found))
(def read-handler-response ::read-handler-response)

;; Delete

(s/def ::delete-handler-response (s/keys))
(def delete-handler-response ::delete-handler-response)

(s/def ::delete-handler-request (s/keys))
(def delete-handler-request ::delete-handler-request)
