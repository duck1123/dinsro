(ns dinsro.specs.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::id pos-int?)
(def id ::id)

(s/def ::name string?)
(def name ::name)

(s/def ::params (s/keys :req [::name]))
(def params ::params)

(s/def ::item-opt (s/keys :opt [::name]))
(def item-opt ::item-opt)

(s/def ::item (s/keys :req [:db/id ::name]))
(def item ::item)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(def schema
  [name-spec])
