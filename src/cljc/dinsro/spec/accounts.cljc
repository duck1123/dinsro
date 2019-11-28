(ns dinsro.spec.accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [orchestra.core :refer [defn-spec]]))

(s/def ::name             string?)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::initial-value    double?)
(def initial-value-spec
  {:db/ident       ::initial-value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id      :db-pos/id)
(s/def ::currency         (s/keys :req [:db/id]))
(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::user-id          :db-pos/id)
(s/def ::user             (s/keys :req [:db/id]))
(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params           (s/keys :req [::name ::initial-value ::currency ::user]))
(s/def ::item             (s/keys :req [:db/id ::name ::initial-value ::currency ::user]))
(def item-spec
  {:db/ident ::item
   :db.entity/attrs [::name ::initial-value ::currency ::user]})

(def schema
  [name-spec
   initial-value-spec
   currency-spec
   user-spec
   #_item-spec])

(comment
  (gen/generate (s/gen ::item))

  )
