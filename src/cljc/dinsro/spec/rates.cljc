(ns dinsro.spec.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.currencies :as s.currencies]))

(s/def ::value (s/and double? pos?))
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
(s/def ::prepared-params (s/keys :req [::rate ::currency]))
(s/def ::item (s/keys :req [:db/id ::value ::currency]))

(comment
  (gen/generate (s/gen ::currency))
  (gen/generate (s/gen ::value))
  (gen/generate (s/gen ::params))
  (gen/generate (s/gen ::prepared-params))
  (gen/generate (s/gen ::item))
  )

(def schema
  [value-spec currency-spec])
