(ns dinsro.model.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]))

(s/def ::id        uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::account ::m.accounts/id)
(defattr account ::account :ref
  {ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.accounts/id
   ::report/column-EQL {::account [::m.accounts/id ::m.accounts/name]}})

(s/def ::transaction ::m.transactions/id)
(defattr transaction ::transaction :ref
  {ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.transactions/id
   ::report/column-EQL {::transaction [::m.transactions/id ::m.transactions/description]}})

(s/def ::value ::ds/valid-double)
(defattr value ::value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::account ::transaction ::value]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id account transaction value])
