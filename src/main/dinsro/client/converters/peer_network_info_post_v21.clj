(ns dinsro.client.converters.peer-network-info-post-v21
  (:require
   [dinsro.client.scala :as cs]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.PeerNetworkInfoPostV21))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/PeerNetworkInfoPostV21.html

(defn PeerNetworkInfoPostV21->record
  [this]
  (let [service-names (map cs/->record
                           (or (some-> this .servicesnames cs/get-or-nil cs/vector->vec) []))
        record        {:addr             (some-> this .addr str)
                       :addr-bind        (some-> this .addrbind str)
                       :addr-local       (some-> this .addrlocal cs/get-or-nil)
                       :bytes-recv       (some-> this .bytesrecv)
                       :connection-time  (some-> this .conntime .toLong)
                       :last-block       (some-> this .last_block .toLong)
                       :last-transaction (some-> this .last_transaction .toLong)
                       :last-recv        (some-> this .lastrecv .toLong)
                       :last-send        (some-> this .lastsend .toLong)
                       :mapped-as        (some-> this .mapped_as cs/get-or-nil)
                       :min-ping         (some-> this .minping cs/get-or-nil)
                       :network          (some-> this .network)
                       :ping-time        (some-> this .pingtime cs/get-or-nil .toLong)
                       :ping-wait        (some-> this .pingwait cs/get-or-nil .toLong)
                       ;; :product-element-names (some-> this .productElementNames)
                       :relay-txes       (some-> this .relaytxes)
                       :services         (some-> this .services)
                       :services-names   service-names
                       :time-offset      (some-> this .timeoffset)}]
    (log/finer :PeerNetworkInfoPostV21->record/finished {:record record})
    record))

(extend-type PeerNetworkInfoPostV21
  cs/Recordable
  (->record [this] (PeerNetworkInfoPostV21->record this)))
