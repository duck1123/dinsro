(ns dinsro.model.contacts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

;; [[../ui/contacts.cljs][Contacts UI]]

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::pubkey string?)
(defattr pubkey ::pubkey :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/cardinality      :one
   ao/required?        true
   ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.users/id
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(>def ::required-params
  (s/keys :req [::name
                ::pubkey]))

(s/def ::params (s/keys :req [::name ::pubkey ::user]))
(s/def ::item (s/keys :req [::id ::name ::pubkey ::user]))

(defn idents [ids] (mapv (fn [id] {::id id}) ids))

(def attributes [id name pubkey user])
