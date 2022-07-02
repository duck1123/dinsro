(ns dinsro.client.converters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   dinsro.client.converters.get-block-result
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResultPostV19))

(>def ::best-block-hash any?)
(>def ::blocks int?)
(>def ::chain string?)
(>def ::chainwork string?)
(>def ::difficulty any?)

(>def ::get-blockchain-info-result-obj (ds/instance? GetBlockChainInfoResultPostV19))
(>def ::get-blockchain-info-result
      (s/keys
       :req-un [::best-block-hash ::blocks ::chain ::chainwork ::difficulty]))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockChainInfoResultPostV19.html

(>defn GetBlockChainInfoResultPostV19->record
  [this]
  [::get-blockchain-info-result-obj => ::get-blockchain-info-result]
  (log/info :GetBlockChainInfoResultPostV19/->record {:this this})
  {:best-block-hash (.bestblockhash this)
   :blocks          (.blocks this)
   :chain           (.name (.chain this))
   :chainwork       (.chainwork this)
   :difficulty      (.difficulty this)
   :headers         (.headers this)})

(extend-type GetBlockChainInfoResultPostV19
  cs/Recordable
  (->record [this] (GetBlockChainInfoResultPostV19->record this)))

;; This call is failing because no wallet is loaded
;; https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#listTransactions(account:String,count:Int,skip:Int,includeWatchOnly:Boolean):scala.concurrent.Future[Vector[org.bitcoins.commons.jsonmodels.bitcoind.ListTransactionsResult]]

(>def ::list-transactions-result (s/keys))

(>def ::fetch-block-by-height-result (s/keys))
