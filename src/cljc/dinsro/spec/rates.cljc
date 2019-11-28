(ns dinsro.spec.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.currencies :as s.currencies]))

(s/def ::rate (s/and double? pos?))
(def rate-spec
  {:db/ident       ::rate
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency (s/keys :req [:db/id]))
(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::rate ::currency]))
(s/def ::item (s/keys :req [:db/id ::rate ::currency]))
(def item-spec
  {:db/ident ::item
   :db.entity/attrs [::rate ::currency]})

(def schema
  [rate-spec currency-spec #_item-spec])

(comment
  (gen/generate (s/gen ::currency))
  (gen/generate (s/gen ::rate))

  (gen/generate (s/gen ::params))
  (gen/generate (s/gen ::item))
  )
