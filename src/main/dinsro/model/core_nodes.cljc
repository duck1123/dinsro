(ns dinsro.model.core-nodes
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::port int?)
(defattr port ::port :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcuser string?)
(defattr rpcuser ::rpcuser :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcpass string?)
(defattr rpcpass ::rpcpass :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr balance :wallet-info/balance :double
  {ao/identities #{::id}
   ao/schema     :production})

(defattr tx-count :wallet-info/tx-count :int
  {ao/identities #{::id}
   ao/schema     :production})

(defattr blocks :blockchain-info/blocks :int
  {ao/identities #{::id}
   ao/schema     :production})

(defattr chain :blockchain-info/chain :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]))
(s/def ::params  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]))
(s/def ::item (s/keys :req [::id ::name ::host ::port ::rpcuser ::rpcpass]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(def attributes [id name host port rpcuser rpcpass balance tx-count blocks chain])
