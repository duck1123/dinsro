(ns dinsro.model.currencies
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::id uuid?)
(def id ::id)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/uuid
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
(def name ::name)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name]))
(def params ::params)

(s/def ::item-opt (s/keys :opt [::name]))
(def item-opt ::item-opt)

(s/def ::item (s/keys :req [::name]))
(def item ::item)

(def schema
  [id-spec
   name-spec])
