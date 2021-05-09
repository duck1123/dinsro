(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(s/def ::id string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id :navlink/id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema :production})

(s/def ::initial-value (s/or :double double? :zero zero?))

(def initial-value-spec
  {:db/ident       ::initial-value
   :db/valueType   :db.type/number
   :db/cardinality :db.cardinality/one})

(defattr initial-value ::initial-value :double
  {ao/identities #{::id}
   ao/schema :production})

(s/def ::currency-id (s/or :id :db/id :zero zero?))
(s/def ::currency
  (s/or :map (s/keys :opt [:db/id ::m.currencies/id])
        :idents (s/coll-of ::m.currencies/ident)))

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(defattr currency ::currency :ref
  {ao/target ::m.currencies/id
   ao/identities #{::id}
   ao/schema :production})

(s/def ::user-id :db/id)

(s/def ::user
  (s/or :map    (s/keys :opt [:db/id ::m.users/id])
        :idents (s/coll-of ::m.users/ident)))

(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(defattr user ::user :ref
  {ao/target ::m.users/id
   ao/identities #{::id}
   ao/schema :production})

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

(def item-spec
  {:db/ident        ::item
   :db.entity/attrs [::name ::initial-value ::currency ::user]})

(def schema
  [currency-spec
   id-spec
   initial-value-spec
   name-spec
   user-spec])

(def attributes [currency id initial-value name user])

#?(:clj
   (def resolvers []))
