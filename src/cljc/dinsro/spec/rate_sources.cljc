(ns dinsro.spec.rate-sources
  (:refer-clojure :exclude [name])
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [tick.alpha.api :as tick]
            [time-specs.core :as ts]))

(s/def ::name             string?)
(def name ::name)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name]))
(s/def ::item (s/keys :req [::name]))

(def schema
  [name-spec])
