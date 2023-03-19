(ns dinsro.model.core.wallet-addresses
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.wallets :as m.c.wallets]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::path-index number?)
(defattr path-index ::path-index :int
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::address uuid?)
(defattr address ::address :ref
  {ao/identities #{::id}
   ao/target     ::m.c.addresses/id
   ao/schema     :production
   ::report/column-EQL {::address [::m.c.addresses/id ::m.c.addresses/address]}})

(>def ::wallet uuid?)
(defattr wallet ::wallet :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.wallets/id
   ao/schema           :production
   ::report/column-EQL {::wallet [::m.c.wallets/id ::m.c.wallets/name]}})

(>def ::params  (s/keys :req [::address ::wallet ::path-index]))
(>def ::item (s/keys :req [::id ::address ::wallet ::path-index]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes
  [id address wallet path-index])
