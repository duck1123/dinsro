(ns dinsro.client.converters.service-identifier
  (:require
   [dinsro.client.scala :as cs]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.p2p.ServiceIdentifier))

;; https://bitcoin-s.org/api/org/bitcoins/core/p2p/ServiceIdentifier.html

(defn ServiceIdentifier->record
  [this]
  (let [record {:network         (some-> this .nodeNetwork)
                :compact-filters (some-> this .nodeCompactFilters)
                :get-utxo        (some-> this .nodeGetUtxo)
                :bloom           (some-> this .nodeBloom)
                :witness         (some-> this .nodeWitness)
                :xthin           (some-> this .nodeXthin)
                :network-limited (some-> this .nodeNetworkLimited)}]

    (log/info :ServiceIdentifier->record/record {:record record})))

(extend-type ServiceIdentifier
  cs/Recordable
  (->record [this] (ServiceIdentifier->record this)))
