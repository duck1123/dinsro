(ns dinsro.options.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.rate-sources :as m.rate-sources]))

(def id ::m.rate-sources/id)

(def name ::m.rate-sources/name)

(def currency ::m.rate-sources/currency)

(def url ::m.rate-sources/url)

(def updated-at ::m.rate-sources/updated-at)

(def active ::m.rate-sources/active)