(ns dinsro.model.ln-channels
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [taoensso.timbre :as log]))

(def rename-map
  {:active         ::active
   :capacity ::capacity
   :chanId ::chan-id
   :channelPoint ::channel-point
   :chanStatusFlags ::chan-status-flags})

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::active boolean?)
(defattr active ::active :boolean
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::capacity number?)
(defattr capacity ::capacity :long
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::chan-id number?)
(defattr chan-id ::chan-id :long
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::channel-point string?)
(defattr channel-point ::channel-point :string
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::chan-status-flags string?)
(defattr chan-status-flags ::chan-status-flags :string
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::close-address string?)
(defattr close-address ::close-address :string
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

(s/def ::commit-fee number?)
(defattr commit-fee ::commit-fee :long
  {ao/identity? true
   ao/target    ::id
   ao/schema    :production})

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

(def attributes [id active capacity chan-id channel-point chan-status-flags close-address commit-fee])
