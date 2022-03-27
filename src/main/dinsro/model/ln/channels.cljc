(ns dinsro.model.ln.channels
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [taoensso.timbre :as log]))

(def rename-map
  {:active                ::active
   :capacity              ::capacity
   :chanId                ::chan-id
   :commitFee             ::commit-fee
   :closeAddress          ::close-address
   :channelPoint          ::channel-point
   :chanStatusFlags       ::chan-status-flags
   :unsettledBalance      ::unsettled-balance
   :remoteBalance         ::remote-balance
   :remoteChanReserveSat  ::remote-chan-reserve-sat
   :localChanReserveSat   ::local-chan-reserve-sat
   :pushAmountSat         ::push-amount-sat
   :commitmentType        ::commitment-typs
   :localBalance          ::local-balance
   :numUpdates            ::num-updates
   :private               ::private?
   :pendingHtlcs          ::pending-htlcs
   :uptime                ::uptime
   :initiator             ::initiator?
   :totalSatoshisReceived ::total-satoshis-received
   :commitWeight          ::commit-weight
   :localConstraints      ::local-constraints
   :feePerKw              ::fee-per-kw
   :lifetime              ::lifetime
   :thawHeight            ::thaw-height
   :totalSatoshisSent     ::total-satoshis-sent
   :remoteConstraints     ::remote-constraints
   :csvDelay              ::csv-delay
   :staticRemoteKey       ::static-remote-key})

(defn prepare-params
  [params]
  (set/rename-keys params rename-map))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::active boolean?)
(defattr active ::active :boolean
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::capacity number?)
(defattr capacity ::capacity :long
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::chan-id number?)
(defattr chan-id ::chan-id :long
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::channel-point string?)
(defattr channel-point ::channel-point :string
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::chan-status-flags string?)
(defattr chan-status-flags ::chan-status-flags :string
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::close-address string?)
(defattr close-address ::close-address :string
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::commit-fee number?)
(defattr commit-fee ::commit-fee :long
  {ao/identities #{::id}
   ao/schema    :production})

(s/def ::local-balance number?)
(defattr local-balance ::local-balance :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::remote-balance number?)
(defattr remote-balance ::remote-balance :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities #{::id}
   ao/target     ::m.ln.nodes/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

;; active :boolean
;; capacity :long
;; chan-id :long
;; channel-point :string
;; chan-status-flags :string
;; close-address :string
;; commit-fee :long
;; commitment-type
;; commit-weight :long
;; csv-delay :int
;; fee-per-kw :long
;; initiator :boolean
;; lifetime :long
;; local-balance :long
;; local-chan-reserve-sat :long
;; num-updates :long

(>defn find-channel
  [_node-id _pubkey]
  [::id ::pubkey => any?]
  (log/info "Finding channel")
  nil)

(s/def ::nodeless-params
  (s/keys :req [::active ::capacity ::chan-id ::channel-point ::chan-status-flags
                ::close-address ::commit-fee]))
(s/def ::params
  (s/keys :req [::active ::capacity ::chan-id ::channel-point ::chan-status-flags
                ::close-address ::commit-fee ::node]))
(s/def ::item
  (s/keys :req [::id ::active ::capacity ::chan-id ::channel-point ::chan-status-flags
                ::close-address ::commit-fee ::node]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [id active capacity chan-id channel-point chan-status-flags
                 close-address commit-fee node local-balance remote-balance])
