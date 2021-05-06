(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(def id            ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/uuid
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::description string?)
(def description ::descriprion)
(def description-spec
  {:db/ident       ::description
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::account
  (s/keys :opt [:db/id
                ::m.accounts/id]))
(def account ::account)
(def account-spec
  {:db/ident       ::account
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date ::ds/date)
(def date ::date)
(def date-spec
  {:db/ident       ::date
   :db/valueType   :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::value ::ds/valid-double)
(def value ::value)
(def value-spec
  {:db/ident       ::value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::account-id :db/id)
(def account-id ::account-id)

(s/def ::required-params (s/keys :req [::date ::description ::value]))
(def required-params ::required-params)

(s/def ::params (s/keys :req [::account ::date ::description ::value]))
(def params ::params)

(s/def ::item (s/keys :req [::id ::account ::date ::description ::value]))
(def item ::item)

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
  [account-spec
   date-spec
   description-spec
   id-spec
   value-spec])
