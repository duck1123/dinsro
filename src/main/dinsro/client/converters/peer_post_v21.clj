(ns dinsro.client.converters.peer-post-v21
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.PeerPostV21))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/PeerPostV21.html

(>def ::synced-headers number?)
(>def ::subver string?)
(>def ::inflight (s/coll-of any?))
(>def ::network-info any?)
(>def ::connection-type string?)
(>def ::id number?)
(>def ::inbound boolean?)
(>def ::version number?)
(>def ::add-node boolean?)
(>def ::synced-blocks number?)

(>def ::record
      (s/keys :req-un
              [::synced-headers ::subver ::inflight ::network-info
               ::connection-type ::id ::inbound ::version
               ::add-node ::synced-blocks]))

(>defn PeerPostV21->record
  [this]
  [(ds/instance? PeerPostV21) => ::record]
  (let [network-info (.networkInfo this)]
    (log/info :PeerPostV21->record/network {:network-info network-info})
    (let [record {:add-node        (.addnode this)
                  :connection-type (.connection_type this)
                  :id              (.id this)
                  :inbound         (.inbound this)
                  :inflight        (some-> this .inflight cs/vector->vec)
                  :network-info    (cs/->record network-info)
                  :subver          (.subver this)
                  :synced-blocks   (.synced_blocks this)
                  :synced-headers  (.synced_headers this)
                  :version         (.version this)}]
      (log/info :PeerPostV21->record/finished {:record record})
      record)))

(extend-type PeerPostV21
  cs/Recordable
  (->record [this] (PeerPostV21->record this)))
