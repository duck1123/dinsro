(ns dinsro.specs.events.forms.add-currency-rate
  (:refer-clojure :exclude [time])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs :as ds]))

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

(s/def ::rate-source-id ::ds/id-string)
(def rate-source-id ::rate-source-id)

(s/def ::add-currency-rate-form
  (s/keys :req-un [::m.rates/date ::m.rates/rate ::m.rates/currency-id]))
(def add-currency-rate-form ::add-currency-rate-form)
