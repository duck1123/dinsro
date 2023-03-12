(ns dinsro.model.ln.info
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(def rename-map
  {:blockHeight         ::block-height
   :syncedToChain       ::synced-to-chain
   :blockHash           ::block-hash
   :syncedToGraph       ::synced-to-graph
   :color               ::color
   :commitHash          ::commit-hash
   :chains              ::chains
   :testnet             ::testnet
   :identityPubkey      ::identity-pubkey
   :numPeers            ::num-peers
   :alias               ::alias
   :numInactiveChannels ::num-inactive-channels
   :bestHeaderTimestamp ::best-header-timestamp
   :numActiveChannels   ::num-active-channels
   :version             ::version
   :numPendingChannels  ::num-pending-channels
   :uris                ::uris
   :features            ::features})

(defattr block-height ::block-height :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(s/def ::alias string?)
(defattr alias-attr ::alias :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(s/def ::identity-pubkey string?)
(defattr identity-pubkey ::identity-pubkey :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr version ::version :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr block-hash ::block-hash :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr color ::color :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr commit-hash ::commit-hash :string
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr testnet ::testnet :boolean
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr synced-to-graph ::synced-to-graph :boolean
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr num-inactive-channels ::num-inactive-channels :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr num-active-channels ::num-active-channels :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr num-pending-channels ::num-pending-channels :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr num-peers ::num-peers :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr uris ::uris :list
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr features ::features :list
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr best-header-timestamp ::best-header-timestamp :int
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(defattr synced-to-chain ::synced-to-chain :boolean
  {ao/identities #{::m.ln.nodes/id}
   ao/schema     :production})

(s/def ::params
  (s/keys
   :req
   [;; ::alias
    ;; ::identity-pubkey
    ;; ::version
    ;; ::block-hash
    ;; ::color
    ;; ::commit-hash
    ;; ::testnet
    ;; ::synced-to-graph
    ;; ::num-inactive-channels
    ;; ::block-height
    ;; ::num-active-channels
    ;; ::num-pending-channels
    ;; ::num-peers
    ;; ::uris
    ;; ::features
    ;; ::best-header-timestamp
    ;; ::synced-to-chain
    ]))

(def attributes
  [;; alias-attr identity-pubkey version block-hash color commit-hash
   ;; testnet synced-to-graph num-inactive-channels block-height
   ;; num-active-channels num-pending-channels num-peers uris features
   ;; best-header-timestamp synced-to-chain
   ])
