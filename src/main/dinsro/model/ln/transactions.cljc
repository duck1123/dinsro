(ns dinsro.model.ln.transactions
  (:refer-clojure :exclude [name])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(def rename-map
  {:amount           ::amount
   :blockHeight      ::block-height
   :blockHash        ::block-hash
   :txHash           ::tx-hash
   :timeStamp        ::time-stamp
   :rawTxHex         ::raw-tx-hex
   :label            ::label
   :description      ::description
   :destAddresses    ::dest-addresses
   :totalFees        ::total-fees})

(defn prepare-params
  [params]
  (set/rename-keys params rename-map))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::amount int?)
(defattr amount ::amount :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block-height int?)
(defattr block-height ::block-height :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block-hash string?)
(defattr block-hash ::block-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::tx-hash string?)
(defattr tx-hash ::tx-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::time-stamp int?)
(defattr time-stamp ::time-stamp :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::raw-tx-hex string?)
(defattr raw-tx-hex ::raw-tx-hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::label string?)
(defattr label ::label :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::dest-addresses any?)
(defattr dest-addresses ::dest-addresses :list
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities #{::id}
   ao/target     ::m.ln.nodes/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::core-tx uuid?)
(defattr core-tx ::core-tx :ref
  {ao/identities #{::id}
   ao/target     ::m.core-tx/id
   ao/schema     :production
   ::report/column-EQL {::core-tx [::m.core-tx/id ::m.core-tx/tx-id]}})

(s/def ::raw-params
  (s/keys :req [::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses]))
(s/def ::params
  (s/keys :req [::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses ::node
                ::core-tx]))
(s/def ::item
  (s/keys :req [::id ::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses ::node ::core-tx]))

(defn idents
  [ids]
  (map (fn [id] {::id id}) ids))

(def attributes
  [id
   amount block-height block-hash
   tx-hash
   time-stamp
   raw-tx-hex
   label
   dest-addresses node core-tx])
