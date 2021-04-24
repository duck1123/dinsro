(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(s/def ::id        uuid?)
(def id            ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/uuid
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::password string?)
(def password ::password)

(s/def ::username string?)
(def username ::username)

(def username-spec
  {:db/ident       ::username
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
(def name ::name)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::password-hash string?)
(def password-hash ::password-hash)

(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::email ::ds/email)
(def email ::email)

(def email-spec
  {:db/ident       ::email
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::input-params-valid (s/keys :req [::name ::email ::password ::username]))
(def input-params-valid ::input-params-valid)

(s/def ::input-params (s/keys :opt [::name ::email ::password ::username]))
(def input-params ::input-params)

(s/def ::params (s/keys :req [::name ::email ::password-hash ::username]))
(def params ::params)

(s/def ::item (s/keys :req [::id ::name ::email ::password-hash ::username]))
(def item ::item)

(def schema
  [email-spec
   id-spec
   name-spec
   password-hash-spec
   username-spec])
