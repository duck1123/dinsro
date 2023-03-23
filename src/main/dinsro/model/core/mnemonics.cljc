(ns dinsro.model.core.mnemonics
  (:refer-clojure :exclude [key name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.users :as m.users]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::entropy string?)
(defattr entropy ::entropy :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::user uuid?)
(defattr user ::user :ref
  {ao/identities       #{::id}
   ao/target           ::m.users/id
   ao/schema           :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(>def ::required-params (s/keys :req [name entropy user]))
(>def ::params (s/keys  :req [name entropy user]))
(>def ::item (s/keys :req [::id name entropy user]))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id name entropy user])
