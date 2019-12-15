(ns dinsro.spec.actions.accounts
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

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

(s/def :delete-account-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-account-request/path-params (s/keys :req-un [:delete-account-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-account-request/path-params]))

(s/def ::delete-handler-response-invalid (s/keys))
(s/def ::delete-handler-response-success (s/keys))
(s/def ::delete-handler-response (s/keys))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (gen/fmap str (s/gen pos-int?)))
  (gen/generate (s/gen :delete-account-request-params/accountId))
  (gen/generate (s/gen ::delete-handler-request))
  (gen/generate (s/gen ::delete-handler-response))
  )
