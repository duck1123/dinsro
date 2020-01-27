(ns dinsro.spec.currencies
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::params (s/keys :req [::name]))
(s/def ::item-opt (s/keys :opt [::name]))
(s/def ::item (s/keys :req [:db/id ::name]))

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(def schema
  [name-spec])
