(ns dinsro.spec.actions.categories
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.categories :as s.categories]
            [taoensso.timbre :as timbre]))

(s/def :create-category-valid/params
  (s/keys :req-un [::s.categories/name ::s.categories/user-id]))
(s/def :create-category/params
  (s/keys :opt-un [::s.categories/name ::s.categories/user-id]))

;; Create

(s/def ::create-handler-request-valid (s/keys :req-un [:create-category-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-category/params]))
(s/def ::create-handler-response (s/keys))

;; Read

(s/def ::read-handler-request ::ds/common-read-request)
(def read-handler-request ::read-handler-request)

(s/def :read-category-response-success/body (s/keys :req-un [::s.categories/item]))
(s/def ::read-handler-response-success (s/keys :req-un [:read-category-response-success/body]))
(def read-handler-response-success ::read-handler-response-success)

(s/def ::read-handler-response (s/or :success   ::read-handler-response-success
                                     :not-found ::ds/common-response-not-found))
(def read-handler-response ::read-handler-response)

;; Delete

(s/def ::delete-handler-request (s/keys :req-un [:common-request-show/path-params]))

(s/def ::delete-handler-response-invalid (s/keys))
(s/def ::delete-handler-response-success (s/keys))
(s/def ::delete-handler-response (s/keys))
(def delete-handler-response ::delete-handler-response)
