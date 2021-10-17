(ns dinsro.model.ln-peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [taoensso.timbre :as log]))

(def rename-map
  {:address    ::address
   :flapCount  ::flap-count
   :inbound    ::inbound
   :pubKey     ::pubkey
   :satRecv    ::sat-recv
   :satSent    ::sat-sent
   :syncType   ::sync-type
   :pingTime   ::ping-time
   :bytesSent  ::bytes-sent
   :bytesRecv  ::bytes-recv
   :lastFlapNs ::last-flap-ns})

;; {:features
;;  [{:key 17, :value {:name "multi-path-payments", :isRequired false, :isKnown true}}
;;   {:key 0, :value {:name "data-loss-protect", :isRequired true, :isKnown true}}
;;   {:key 5, :value {:name "upfront-shutdown-script", :isRequired false, :isKnown true}}
;;   {:key 7, :value {:name "gossip-queries", :isRequired false, :isKnown true}}
;;   {:key 9, :value {:name "tlv-onion", :isRequired false, :isKnown true}}
;;   {:key 12, :value {:name "static-remote-key", :isRequired true, :isKnown true}}
;;   {:key 14, :value {:name "payment-addr", :isRequired true, :isKnown true}}],
;;  :errors [],
;;  :address "10.43.107.98:9735",
;;  :flapCount 1,
;;  :bytesSent 141,
;;  :bytesRecv 141,
;;  :syncType "ACTIVE_SYNC",
;;  :satRecv 0,
;;  :pubKey "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544",
;;  :lastFlapNs 1632677413835970628,
;;  :pingTime 0,
;;  :inbound false,
;;  :satSent 0
;;  }

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/target    ::m.ln-nodes/id
   ao/identities #{::id}
   ao/schema    :production})

;; address
(s/def ::address string?)
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

;; bytesSent
;; flapCount
;; bytesRecv
;; syncType
;; satRecv
;; pubKey

(s/def ::pubkey (s/or
                 :nil nil?
                 :string string?))
(defattr pubkey ::pubkey :string
  {ao/identities #{::id}
   ao/schema     :production})

;; lastFlapNs
;; pingTime

(s/def ::inbound boolean?)
(defattr inbound ::inbound :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sat-sent number?)
(defattr sat-sent ::sat-sent :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sat-recv number?)
(defattr sat-recv ::sat-recv :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params (s/keys :req [::address ::pubkey ::inbound ::sat-sent ::sat-recv]))
(s/def ::item (s/keys :req [::id ::address ::pubkey ::inbound ::sat-sent ::sat-recv ::node]))

(def attributes [id address pubkey inbound sat-sent node])
