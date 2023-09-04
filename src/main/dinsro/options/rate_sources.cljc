(ns dinsro.options.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.rate-sources :as m.rate-sources]))

(def active
  "Boolean flag determining if this source is currently active"
  ::m.rate-sources/active)

(def id
  "The ID of the rate source"
  ::m.rate-sources/id)

(def name
  "The name of the rate source"
  ::m.rate-sources/name)

(def currency
  "The currency this source is fetching rates for"
  ::m.rate-sources/currency)

(def url ::m.rate-sources/url)

(def updated-at
  "Timstamp of last successful rate update"
  ::m.rate-sources/updated-at)

