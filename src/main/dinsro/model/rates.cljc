(ns dinsro.model.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs :as ds]))

(s/def ::id        string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::rate ds/valid-double)
(def rate-spec
  {:db/ident       ::rate
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id ::m.currencies/id)

(s/def ::currency
  (s/keys :opt [:db/id
                ::m.currencies/id]))
(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date ds/date)
(s/def ::date-ms pos-int?)
(s/def ::date-inst inst?)

(def date-spec
  {:db/ident       ::date
   :db/valueType   :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::required-params (s/keys :req [::rate ::date]))

(s/def ::params (s/keys :req [::rate ::currency ::date]))

(s/def ::item (s/keys :req [::id ::rate ::currency ::date]))

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(def schema
  [currency-spec
   date-spec
   id-spec
   rate-spec])

(s/def ::rate-feed-item (s/cat :date ::date-ms
                               :rate ::rate))

(s/def ::rate-feed (s/coll-of ::rate-feed-item))
