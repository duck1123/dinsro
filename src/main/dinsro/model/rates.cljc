(ns dinsro.model.rates
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(def id            ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/uuid
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::rate ds/valid-double)
(def rate ::rate)
(def rate-spec
  {:db/ident       ::rate
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id ::m.currencies/id)

(s/def ::currency
  (s/keys :opt [:db/id
                ::m.currencies/id]))
(def currency ::currency)
(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date ds/date)
(def date ::date)
(s/def ::date-ms pos-int?)
(def date-ms ::date-ms)
(s/def ::date-inst inst?)
(def date-inst ::date-inst)

(def date-spec
  {:db/ident       ::date
   :db/valueType   :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::required-params (s/keys :req [::rate ::date]))
(def required-params ::required-params)

(s/def ::params (s/keys :req [::rate ::currency ::date]))
(def params ::params)

(s/def ::item (s/keys :req [::id ::rate ::currency ::date]))
(def item ::item)

(def schema
  [currency-spec
   date-spec
   id-spec
   rate-spec])

(s/def ::rate-feed-item (s/cat :date ::date-ms
                               :rate ::rate))
(def rate-feed-item ::rate-feed-item)

(s/def ::rate-feed (s/coll-of ::rate-feed-item))
(def rate-feed ::rate-feed)
