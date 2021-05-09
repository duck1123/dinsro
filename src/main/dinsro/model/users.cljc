(ns dinsro.model.users
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [taoensso.timbre :as log]))

(s/def ::password string?)

(s/def ::id string?)

(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id ::id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::password-hash string?)

(def password-hash-spec
  {:db/ident       ::password-hash
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr password-hash ::password-hash :string
  {ao/required?       true
   ao/identities      #{:account/id}
   ::auth/permissions (fn [_] #{})
   ;; :com.fulcrologic.rad.database-adapters.sql/column-name "password"
   ao/schema          :production})

(s/def ::input-params-valid (s/keys :req [::password ::id]))

(s/def ::input-params (s/keys :opt [::password ::id]))

(s/def ::params (s/keys :req [::password-hash ::id]))

(s/def ::item (s/keys :req [::password-hash ::id]))

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

(def attributes [id password-hash])

#?(:clj
   (def resolvers []))
