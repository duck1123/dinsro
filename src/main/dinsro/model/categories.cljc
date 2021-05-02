(ns dinsro.model.categories
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

(s/def ::id        string?)
(def id            ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
(def name ::name)
(def name-spec
  {:db/ident       name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::user-id :db/id)
(def user-id ::user-id)

(s/def ::user (s/keys :req [::m.users/id]))
(def user ::user)
(def user-spec
  {:db/ident       user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name ::user]))
(def params ::params)

(s/def ::item (s/keys :req [::id ::name ::user]))
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
  [id-spec
   name-spec
   user-spec])
