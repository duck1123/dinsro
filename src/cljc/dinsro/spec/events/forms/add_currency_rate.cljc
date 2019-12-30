(ns dinsro.spec.events.forms.add-currency-rate
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.rates :as s.rates]))

(s/def ::valid boolean?)

(s/def ::shown? boolean?)

(def default-rate 1)

(s/def ::date string?)
(s/def ::rate string?)
(s/def ::time string?)
(s/def ::currency-id string?)

(s/def ::add-currency-rate-form
  (s/keys :req-un [::s.rates/date ::s.rates/rate ::s.rates/currency-id]))
