(ns dinsro.spec.events.forms.create-rate
  (:refer-clojure :exclude [time])
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]))

(s/def ::rate string?)
(def rate ::rate)

(s/def ::currency-id ds/id-string-opt)
(def currency-id ::currency-id)

(s/def ::date string?)
(def date ::date)

(s/def ::time string?)
(def time ::time)

(s/def ::shown? boolean?)
(def shown? ::shown?)
