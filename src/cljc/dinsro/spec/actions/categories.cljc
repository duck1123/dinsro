(ns dinsro.spec.actions.categories
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.categories :as s.categories]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def :create-category-valid/params
  (s/keys :req-un [
                   ::s.categories/name
                   ;; ::s.accounts/initial-value
                   ::s.categories/user-id
                   ;; ::s.accounts/currency-id
                   ]))
(s/def :create-category/params
  (s/keys :opt-un [
                   ::s.categories/name
                   ;; ::s.accounts/initial-value
                   ::s.categories/user-id
                   ;; ::s.accounts/currency-id
                   ]))

(s/def ::create-handler-request-valid (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(s/def ::create-handler-response (s/keys))

(s/def :delete-category-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-category-request/path-params (s/keys :req-un [:delete-handler-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-handler-request/path-params]))

(s/def ::delete-handler-response-invalid (s/keys))
(s/def ::delete-handler-response-success (s/keys))
(s/def ::delete-handler-response (s/keys))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (gen/fmap str (s/gen pos-int?)))
  (gen/generate (s/gen :delete-category-request-params/id))
  (gen/generate (s/gen ::delete-handler-request))
  (gen/generate (s/gen ::delete-handler-response))
  )
