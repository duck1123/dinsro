(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.currencies :as m.currencies]
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
(s/def ::params
  (s/keys :req [::name
                ::initial-value]
          :opt [::currency
                ::user]))

(s/def ::item (s/keys :req [::id ::name ::initial-value ::user]
                      :opt [::currency]))
(defattr all-accounts ::all-accounts :ref
  {ao/target    ::id
   ao/pc-output [{::all-accounts [::id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (comment env query-params)
     {::all-accounts
      #?(:clj  (queries/get-all-accounts env query-params)
         :cljs [])})})

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

(def attributes [account-transactions all-accounts currency id initial-value link name user])

#?(:clj (def resolvers []))
