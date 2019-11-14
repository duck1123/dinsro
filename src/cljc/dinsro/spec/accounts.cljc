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
    :db/cardinality :db.cardinality/one}])

(s/def ::id      pos-int?)
(s/def ::name    string?)
(s/def ::params  (s/keys :req [::name]))
(s/def ::item (s/keys :req [::name]))
