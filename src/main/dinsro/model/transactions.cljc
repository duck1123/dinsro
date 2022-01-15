(ns dinsro.model.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::description string?)
(defattr description ::description :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::account ::m.accounts/id)
(defattr account ::account :ref
  {ao/identities #{::id}
   ao/schema     :production
   ao/target     ::m.accounts/id
   ::report/column-EQL {::account [::m.accounts/id ::m.accounts/name]}})

(s/def ::date ::ds/date)
(defattr date ::date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value ::ds/valid-double)
(defattr value ::value :double
  {ao/identities #{::id}
   ao/schema     :production})

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

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [account date description id value])

#?(:clj (def resolvers []))
