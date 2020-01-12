(ns dinsro.spec.events.forms.create-currency
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(def default-name "Foo")

(s/def ::name string?)
(def name ::name)

(s/def ::shown? boolean?)
(def shown? ::shown?)
