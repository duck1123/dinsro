(ns dinsro.specs.rates
  (:require [clojure.spec.alpha :as s]))

(s/def ::value double?)
(s/def ::params (s/keys :req [::value]))
(s/def ::prepared-params (s/keys :req [::value]))
(s/def ::item (s/keys :req [:db/id ::value]))

(def schema
  [{:db/ident ::value
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}])
