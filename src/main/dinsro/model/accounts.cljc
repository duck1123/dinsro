(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(comment ::auth/_ ::pc/_)

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::initial-value (s/or :double double? :zero zero?))

(defattr initial-value ::initial-value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/cardinality :one
   ao/required?   true
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.currencies/id})

(s/def ::source ::m.rate-sources/id)
(defattr source ::source :ref
  {ao/cardinality :one
   ao/required?   true
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.rate-sources/id})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/cardinality :one
   ao/required?   true
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.users/id})

(s/def ::required-params
  (s/keys :req [::name
                ::initial-value]))

(def required-params
  "Required params for accounts"
  ::required-params)
(s/def ::params (s/keys :req [::currency ::initial-value ::name ::source ::user]))
(s/def ::item (s/keys :req [::id ::currency ::initial-value ::name ::source ::user]))

(defattr link ::link :ref
  {ao/cardinality :one
   ao/target      ::id
   ao/pc-input    #{::id}
   ao/pc-output   [{::link [::id]}]
   ao/pc-resolve  (fn [_env params] {::link params})})

(defattr account-transactions ::account-transactions :ref
  {ao/cardinality :one
   ao/target     ::id
   ao/pc-input   #{::id}
   ao/pc-output  [{::account-transactions [::id]}]
   ao/pc-resolve (fn [_env params] {::account-transactions params})})

(def attributes [account-transactions currency id initial-value link name source user])

#?(:clj (def resolvers []))
