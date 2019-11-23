(ns dinsro.spec.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::value (s/and double? pos?))
(s/def ::currency-id :db-pos/id)

(s/def ::params (s/keys :req [::value ::currency-id]))
(s/def ::prepared-params (s/keys :req [::value ::currency-id]))
(s/def ::item (s/keys :req [:db/id ::value ::currency-id]))

(comment
  (gen/generate (s/gen ::currency-id))
  (gen/generate (s/gen ::value))
  (gen/generate (s/gen ::params))
  (gen/generate (s/gen ::prepared-params))
  (gen/generate (s/gen ::item))
  )

(def schema
  [{:db/ident ::value
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}])
