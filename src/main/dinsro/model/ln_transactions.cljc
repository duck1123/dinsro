(ns dinsro.model.ln-transactions
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [taoensso.timbre :as log]))

(def rename-map
  {:numConfirmations ::num-confirmations
   :amount           ::amount
   :blockHeight      ::block-height
   :blockHash        ::block-hash
   :txHash           ::tx-hash
   :timeStamp        ::time-stamp
   :rawTxHex         ::raw-tx-hex
   :label            ::label
   :description      ::description
   :destAddresses    ::dest-addresses
   :totalFees        ::total-fees})

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::num-confirmations int?)
(defattr num-confirmations ::num-confirmations :int
  {ao/identities #{::id}
   ao/schema     :production})

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
   ao/target     ::m.ln-nodes/id
   ao/schema     :production})

(s/def ::raw-params
  (s/keys :req [::num-confirmations ::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses]))
(s/def ::params
  (s/keys :req [::num-confirmations ::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses ::node]))
(s/def ::item
  (s/keys :req [::id ::num-confirmations ::amount ::block-height ::block-hash ::tx-hash
                ::time-stamp ::raw-tx-hex ::label ::dest-addresses ::node]))

(def attributes
  [id num-confirmations amount block-height block-hash tx-hash
   time-stamp raw-tx-hex label dest-addresses node])
