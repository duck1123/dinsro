(ns dinsro.spec.transactions
  (:require [cljc.java-time.extn.predicates :as predicates]
            [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            #_[dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [tick.alpha.api :as tick]
            [time-specs.core :as ts]
            ))

(s/def ::account (s/keys :req [:ds/id]))
(def account ::account)
(def account-spec
  {:db/ident ::account
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::currency (s/keys))
(def currency ::currency)
(def currency-spec
  {:db/ident ::currency
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date ts/instant?)
(def date ::date)
(def date-spec
  {:db/ident ::date
   :db/valueType :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::value ::ds/valid-double)
(def value ::value)
(def value-spec
  {:db/ident       ::value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::account-id  ::ds/id)
(s/def ::currency-id ::ds/id)

(s/def ::params (s/keys :req [::account ::currency ::date ::value]))
(def params ::params)
(s/def ::item (s/keys :req [::account ::currency ::date ::value]))
(def item ::item)
(def schema
  [value-spec currency-spec date-spec account-spec])

(comment
  (ds/gen-key ::date)
  (ds/gen-key ::account)
  (ds/gen-key ::date)
  (ds/gen-key params)
  )
