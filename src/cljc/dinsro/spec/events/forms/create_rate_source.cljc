(ns dinsro.spec.events.forms.create-rate-source
  (:refer-clojure :exclude [name])
  (:require [clojure.spec.alpha :as s]))

(def default-name "Default rate source")

(s/def ::name string?)
(def name ::name)
