(ns dinsro.options.currencies
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.currencies :as m.currencies]))

(def id ::m.currencies/id)

(def code ::m.currencies/code)

(def name ::m.currencies/name)