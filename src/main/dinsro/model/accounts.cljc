(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs]))

(s/def ::id uuid?)
(def id
  "Primary id of the account"
  ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/uuid
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
(def name
  "User-assigned name for the account"
  ::name)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::initial-value (s/or :double double? :zero zero?))

(def initial-value ::initial-value)
(def initial-value-spec
  {:db/ident       ::initial-value
   :db/valueType   :db.type/number
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id (s/or :id :db/id :zero zero?))
(def currency-id ::currency-id)
(s/def ::currency (s/keys :req [:db/id]))
(def currency ::currency)

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::user-id :db/id)
(def user-id ::user-id)

(s/def ::user (s/keys :req [:db/id]))
(def user ::user)

(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name ::initial-value ::user]
                        :opt [::currency]))
(def params ::params)

(s/def ::item (s/keys :req [:db/id ::name ::initial-value ::user]
                      :opt [::currency]))
(def item ::item)

(def item-spec
  {:db/ident        ::item
   :db.entity/attrs [::name ::initial-value ::currency ::user]})

(def schema
  [currency-spec
   id-spec
   initial-value-spec
   name-spec
   user-spec])
