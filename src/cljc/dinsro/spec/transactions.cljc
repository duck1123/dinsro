(ns dinsro.spec.transactions
  (:require [cljc.java-time.extn.predicates :as predicates]
            [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            #_[dinsro.spec.currencies :as s.currencies]))

(s/def ::account (s/keys))
(def account-spec
  {::db/ident ::account
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::currency (s/keys))
(def currency-spec
  {}
  )

(s/def ::date inst?)
(def date-spec
  {}
  )

(s/def ::value double?)
(def value-spec
  {})

(s/def ::item (s/keys :req [::account ::currency ::date ::value]))
(def schema
  [value-spec currency-spec date-spec account-spec])
