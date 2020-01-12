(ns dinsro.spec.events.forms.add-currency-rate
  (:refer-clojure :exclude [time])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.rates :as s.rates]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::date string?)
(def date ::date)

(s/def ::rate string?)
(def rate ::rate)

(s/def ::time string?)
(def time ::time)

(s/def ::currency-id string?)
(def currency-id ::currency-id)

(s/def ::add-currency-rate-form
  (s/keys :req-un [::s.rates/date ::s.rates/rate ::s.rates/currency-id]))
(def add-currency-rate-form ::add-currency-rate-form)
