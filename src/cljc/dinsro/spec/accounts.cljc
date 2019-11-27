(ns dinsro.spec.accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [orchestra.core :refer [defn-spec]]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::initial-value
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::currency
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::user
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one}])

(s/def ::id      pos-int?)
(s/def ::initial-value    double?)
(s/def ::name    string?)
(s/def ::params  (s/keys :req [::name]))
(s/def ::currency (s/keys :req [:db/id]))
(s/def ::user (s/keys :req [:db/id]))
(s/def ::item (s/keys :req [::name ::initial-value ::currency ::user]))
