(ns dinsro.model.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs]))

(s/def ::id        string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id ::id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::url string?)

(def url-spec
  {:db/ident       ::url
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr url ::url :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency
  (s/keys :opt [:db/id
                ::m.currencies/id]))

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(defattr currency ::currency :ref
  {ao/target     ::m.currencies/id
   ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::name ::url]))

(s/def ::params (s/keys :req [::name ::url ::currency]))

(s/def ::item (s/keys :req [::id ::name ::url ::currency]))

(s/def ::items (s/coll-of ::item))

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
  [currency-spec
   id-spec
   name-spec
   url-spec])

(def attributes [currency id name url])

#?(:clj
   (def resolvers []))
