(ns dinsro.spec.actions.categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]
   [dinsro.spec.categories :as s.categories]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid (s/keys :req-un [::s.categories/name ::s.categories/user-id]))
(def create-params-valid ::create-params-valid)

(s/def ::create-params (s/keys :opt-un [::s.categories/name ::s.categories/user-id]))
(def create-params ::create-params)

(s/def :create-category/params ::create-params)

;; Create

(s/def :create-category-valid/params ::create-params-valid)
(s/def ::create-request-valid (s/keys :req-un [:create-category-valid/params]))
(def create-request-valid ::create-request-valid)

(s/def ::create-request (s/keys :req-un [:create-category/params]))
(def create-request ::create-request)

(s/def ::create-response (s/keys))
(def create-response ::create-response)

;; Read

(s/def ::read-request ::ds/common-read-request)
(def read-request ::read-request)

(s/def :read-category-response-success/body ::s.categories/item #_(s/keys :req-un []))
(s/def :read-category-response-success/status #{200})
(s/def ::read-response-success (s/keys :req-un [:read-category-response-success/body]))
(def read-response-success ::read-response-success)

(s/def ::read-response (s/or :success   ::read-response-success
                                     :not-found ::ds/common-response-not-found))
(def read-response ::read-response)

;; Delete

(s/def ::delete-request (s/keys :req-un [:common-request-show/path-params]))

(s/def ::delete-response-invalid (s/keys))
(s/def ::delete-response-success (s/keys))
(s/def ::delete-response (s/keys))
(def delete-response ::delete-response)

(s/def ::index-request (s/keys))
(s/def ::index-response (s/keys))
