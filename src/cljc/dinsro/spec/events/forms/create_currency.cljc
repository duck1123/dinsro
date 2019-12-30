(ns dinsro.spec.events.forms.create-currency
  (:require [clojure.spec.alpha :as s]))

(def default-name "Foo")

(s/def ::name string?)
(s/def ::shown? boolean?)
