(ns dinsro.model.wallet-addresses
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.wallets :as m.wallets]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::wallet uuid?)
(defattr wallet ::wallet :ref
  {ao/identities       #{::id}
   ao/target           ::m.wallets/id
   ao/schema           :production
   ::report/column-EQL {::wallet [::m.wallets/id ::m.wallets/name]}})

(s/def ::params  (s/keys :req [::address ::wallet]))
(s/def ::item (s/keys :req [::id ::address ::wallet]))

(defn ident
  [id]
  {::id id})

(defn idents
  [ids]
  (mapv ident ids))

(def attributes
  [id address wallet])
