(ns dinsro.client.converters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   dinsro.client.converters.get-blockchain-info-result
   dinsro.client.converters.get-block-result
   dinsro.client.converters.peer-network-info-post-v21
   dinsro.client.converters.peer-post-v21
   dinsro.client.converters.service-identifier
   [dinsro.client.scala :as cs])
  (:import
   org.bitcoins.crypto.DoubleSha256DigestBE))

(defn DoubleSha256DigestBE->record
  [this]
  (.hex this))

;; https://bitcoin-s.org/api/org/bitcoins/crypto/DoubleSha256DigestBE.html

(extend-type DoubleSha256DigestBE
  cs/Recordable
  (->record [this] (DoubleSha256DigestBE->record this)))

;; This call is failing because no wallet is loaded
;; https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#listTransactions(account:String,count:Int,skip:Int,includeWatchOnly:Boolean):scala.concurrent.Future[Vector[org.bitcoins.commons.jsonmodels.bitcoind.ListTransactionsResult]]

(>def ::list-transactions-result (s/keys))

(>def ::fetch-block-by-height-result (s/keys))
