(ns dinsro.spec.events.forms.create-rate-source
  (:refer-clojure :exclude [name])
  (:require [clojure.spec.alpha :as s]))

(def default-name "Default rate source")
(def default-url "http://example.com/")
(def default-currency-id 53)

(s/def ::name string?)
(def name ::name)

(s/def ::url string?)
(def url ::url)

(s/def ::currency-id string?)
(def currency-id ::currency-id)
