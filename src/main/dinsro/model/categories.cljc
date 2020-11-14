(ns dinsro.model.categories
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]))

(s/def ::name string?)
(def name ::name)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::user-id ::ds/id)
(def user-id ::user-id)

(s/def ::user (s/keys :req [:db/id]))
(def user ::user)
(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name ::user]))
(def params ::params)

(s/def ::item (s/keys :req [:db/id ::name ::user]))
(def item ::item)

(def schema
  [name-spec user-spec])
