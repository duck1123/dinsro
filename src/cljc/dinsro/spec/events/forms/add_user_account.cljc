(ns dinsro.spec.events.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::name string?)
(def name ::name)

(s/def ::initial-value number?)
(def initial-value ::initial-value)

(s/def ::currency-id ds/id-string)
(def currency-id ::currency-id)

(s/def ::user-id ds/id-string)
(def user-id ::user-id)

(s/def ::create-handler-request
  (s/keys :req-un [::s.accounts/name]))
(def create-handler-request ::create-handler-request)

(s/def ::form-bindings (s/cat
                        :name ::name
                        :initial-value ::initial-value
                        :currency-id ::currency-id
                        :user-id ::user-id))
(def form-bindings ::form-bindings)
