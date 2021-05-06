(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [taoensso.timbre :as timbre]))

(s/def ::password string?)
(def password ::password)

(s/def ::username string?)
(def username ::username)

(def username-spec
  {:db/ident       ::username
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::password-hash string?)
(def password-hash ::password-hash)

(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::input-params-valid (s/keys :req [::password ::username]))
(def input-params-valid ::input-params-valid)

(s/def ::input-params (s/keys :opt [::password ::username]))
(def input-params ::input-params)

(s/def ::params (s/keys :req [::password-hash ::username]))
(def params ::params)

(s/def ::item (s/keys :req [::password-hash ::username]))
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
  [password-hash-spec
   username-spec])
