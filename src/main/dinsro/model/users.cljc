(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [taoensso.timbre :as log]))

(s/def ::password string?)

(s/def ::id string?)

(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::password-hash string?)

(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::input-params-valid
  (s/keys :req [::name ::email ::password ::username]))

(s/def ::input-params
  (s/keys :opt [::name ::email ::password ::username]))

(s/def ::params
  (s/keys :req [::password-hash ::username]
          :opt [::name ::email]))

(s/def ::item
  (s/keys :req [::password-hash ::username]
          :opt [::name ::email]))

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
  [password-hash-spec
   id-spec])

(def attributes [])

#?(:clj
   (def resolvers []))
