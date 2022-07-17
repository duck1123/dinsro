(ns dinsro.client.converters.peer-post-v21
  (:require
   [dinsro.client.scala :as cs]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.PeerPostV21))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/PeerPostV21.html

(defn PeerPostV21->record
  [this]
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
