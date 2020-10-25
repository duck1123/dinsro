(ns dinsro.spec.events.forms.create-rate
  (:refer-clojure :exclude [time])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]))

(s/def ::rate ::ds/double-string)
(def rate ::rate)

(s/def ::currency-id ::ds/id-string-opt)
(def currency-id ::currency-id)

(s/def ::rate-source-id ::ds/id-string)
(def rate-source-id ::rate-source-id)

(s/def ::date ::ds/date-string)
(def date ::date)

(s/def ::time string?)
(def time ::time)

(s/def ::shown? boolean?)
(def shown? ::shown?)
