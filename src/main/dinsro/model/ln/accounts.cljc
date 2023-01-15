(ns dinsro.model.ln.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address-type string?)
(defattr address-type ::address-type :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::master-key-fingerprint string?)
(defattr master-key-fingerprint ::master-key-fingerprint :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::wallet uuid?)
(defattr wallet ::wallet :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.wallets/id
   ao/schema           :production
   ::report/column-EQL {::wallet [::m.c.wallets/id ::m.c.wallets/name]}})

(s/def ::params
  (s/keys :req [::node ::wallet]))
(s/def ::item
  (s/keys :req [::id ::node ::wallet]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes
  [id node address-type wallet
   master-key-fingerprint])
