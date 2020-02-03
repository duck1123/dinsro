(ns dinsro.spec.events.forms.create-transaction
  (:require
   [clojure.spec.alpha :as s]
   [taoensso.timbre :as timbre]))

(s/def ::account-id string?)
(def account-id ::account-id)

(s/def ::date string?)
(def date ::date)

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::value string?)
(def value ::value)

(s/def ::description string?)
(def description ::description)
