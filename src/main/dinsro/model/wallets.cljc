(ns dinsro.model.wallets
  (:refer-clojure :exclude [key name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core-nodes :as m.core-nodes]
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

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities #{::id}
   ao/target     ::m.core-nodes/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.core-nodes/id ::m.core-nodes/name]}})

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

(defn idents
  [ids]
  (map (fn [id] {::id id}) ids))

(def attributes [id name derivation key node user])
