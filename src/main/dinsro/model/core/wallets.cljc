(ns dinsro.model.core.wallets
  (:refer-clojure :exclude [key name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::derivation string?)
(defattr derivation ::derivation :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::key string?)
(defattr key ::key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::seed (s/coll-of string?))
(defattr seed ::seed :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities #{::id}
   ao/target     ::m.c.nodes/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.c.nodes/id ::m.c.nodes/name]}})

(s/def ::user uuid?)
(defattr user ::user :ref
  {ao/identities #{::id}
   ao/target     ::m.users/id
   ao/schema     :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(s/def ::required-params (s/keys :req [::name]))
(s/def ::params  (s/keys :req [::name]))
(s/def ::item (s/keys :req [::id ::name]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))
(s/def ::ident-map (s/keys :req [::id]))

(>defn ident
  [id]
  [::id => ::ident-map]
  {::id id})

(>defn idents
  [ids]
  [(s/coll-of ::id) => (s/coll-of ::ident-map)]
  (mapv ident ids))

(def attributes [id name derivation key node user seed])
