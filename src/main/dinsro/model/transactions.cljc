(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]))

(s/def ::id        string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(defattr id :navlink/id :string
  {ao/identity? true
   ao/schema    :production})

(s/def ::description string?)
(def description-spec
  {:db/ident       ::description
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema :production})

(s/def ::account
  (s/keys :opt [:db/id
                ::m.accounts/id]))
(def account-spec
  {:db/ident       ::account
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(defattr account ::account :ref
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::date ::ds/date)
(def date-spec
  {:db/ident       ::date
   :db/valueType   :db.type/instant
   :db/cardinality :db.cardinality/one})

(defattr date ::date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value ::ds/valid-double)
(def value-spec
  {:db/ident       ::value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(defattr value ::value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::account-id :db/id)

(s/def ::required-params (s/keys :req [::date ::description ::value]))

(s/def ::params (s/keys :req [::account ::date ::description ::value]))

(s/def ::item (s/keys :req [::id ::account ::date ::description ::value]))

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
  [account-spec
   date-spec
   description-spec
   id-spec
   value-spec])

(def attributes [account date description id value])

#?(:clj
   (def resolvers []))
