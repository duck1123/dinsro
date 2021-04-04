(ns dinsro.specs.events.forms.create-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(s/def ::account-id :db/id)
(def account-id ::account-id)

(s/def ::date string?)
(def date ::date)

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::value
  ;; ::ds/double-string
  ::ds/valid-double)
(def value ::value)

(s/def ::description string?)
(def description ::description)
