(ns dinsro.client.converters.peer
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   lnrpc.Peer
   ;; lnrpc.Peer.SyncType
   ))

(defn ->obj
  ([]
   (let [pub-key ""]
     (->obj pub-key)))

  ([pub-key]
   (let [address ""]
     (->obj pub-key address)))

  ([pub-key address]
   (let [bytes-sent (cs/uint64 0)]
     (->obj pub-key address bytes-sent)))

  ([pub-key address bytes-sent]
   (let [bytes-recv (cs/uint64 0)]
     (->obj pub-key address bytes-sent bytes-recv)))

  ([pub-key address bytes-sent bytes-recv]
   (let [sat-sent 0]
     (->obj pub-key address bytes-sent bytes-recv sat-sent)))

  ([pub-key address bytes-sent bytes-recv sat-sent]
   (let [sat-recv 0]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv]
   (let [inbound? false]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound?)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound?]
   (let [ping-time 0]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time]
   (let [sync-type nil]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type]
   (let [features nil]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features]
   (let [errors nil]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors]
   (let [flap-count (int 0)]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count]
   (let [last-flap-ns 0]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns)))

  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns]
   (let [last-ping-payload nil]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns last-ping-payload)))
  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns last-ping-payload]
   (let [unknown-fields nil]
     (->obj pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns last-ping-payload unknown-fields)))
  ([pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns last-ping-payload unknown-fields]
   (Peer. pub-key address bytes-sent bytes-recv sat-sent sat-recv inbound? ping-time sync-type features errors flap-count last-flap-ns last-ping-payload unknown-fields)))

(>def ::record (s/keys))

;; https://bitcoin-s.org/api/lnrpc/Peer.html

(>defn Peer->record
  [this]
  [(ds/instance? Peer) => ::record]
  (let [record {::pubkey     (some-> this .pubKey)
                ::address    (some-> this .address)
                ::bytes-sent (some-> this .bytesSent  .toLong)
                ::bytes-recv (some-> this .bytesRecv  .toLong)
                ::sat-sent   (some-> this .satSent)
                ::sat-recv   (some-> this .satRecv)
                ::inbound?   (some-> this .inbound)
                ::ping-time  (some-> this .pingTime)
                ::sync-type  (some-> this .syncType str)
                ;; ::features   (some-> this .features)
                }]

    (log/info :Peer->record/finished {:record record})
    record))

(extend-type Peer
  cs/Recordable
  (->record [this] (Peer->record this)))
