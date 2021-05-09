(ns dinsro.model.categories
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

(s/def ::id        string?)
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
   ao/schema     :production})

(s/def ::user-id :db/id)

(s/def ::user (s/keys :req [::m.users/id]))
(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(defattr user ::user :ref
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::name ::user]))

(s/def ::item (s/keys :req [::id ::name ::user]))

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

(def attributes [id name user])

#?(:clj
   (def resolvers []))
