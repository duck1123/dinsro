(ns dinsro.spec.events.forms.create-account
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]))

(def default-name "Offshore")
(def default-initial-value 1.0)

(s/def ::name string?)
(def name ::name)

(s/def ::currency-id ::ds/id-string)
(def currency-id ::currency-id)

(s/def ::user-id ::ds/id-string)
(def user-id ::user-id)

(s/def ::shown? boolean?)
(def shown? shown?)

(s/def ::initial-value ::ds/double-string)
(def initial-value ::initial-value)
