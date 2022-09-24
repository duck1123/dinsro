(ns dinsro.client.converters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   dinsro.client.converters.account
   dinsro.client.converters.address-type
   dinsro.client.converters.bitcoin-address
   dinsro.client.converters.byte-vector
   dinsro.client.converters.currency-unit
   dinsro.client.converters.get-block-result
   dinsro.client.converters.get-blockchain-info-result
   dinsro.client.converters.get-raw-transaction-result
   dinsro.client.converters.get-raw-transaction-vin
   dinsro.client.converters.peer-network-info-post-v21
   dinsro.client.converters.peer-post-v21
   dinsro.client.converters.rpc-transaction-output
   dinsro.client.converters.script-pub-key
   dinsro.client.converters.service-identifier
   dinsro.client.converters.transaction-input
   dinsro.client.converters.transaction-out-point
   dinsro.client.converters.transaction-output
   dinsro.client.converters.witness-transaction
   [dinsro.client.scala :as cs])
  (:import
   org.bitcoins.crypto.DoubleSha256DigestBE
   org.bitcoins.core.protocol.script.MultiSignatureScriptSignature))

(defn DoubleSha256DigestBE->record
  [this]
  (.hex this))

;; https://bitcoin-s.org/api/org/bitcoins/crypto/DoubleSha256DigestBE.html

(extend-type DoubleSha256DigestBE
  cs/Recordable
  (->record [this] (DoubleSha256DigestBE->record this)))

(extend-type MultiSignatureScriptSignature
  cs/Recordable
  (->record [this] (.hex this)))

;; This call is failing because no wallet is loaded
;; https://bitcoin-s.org/api/org/bitcoins/rpc/client/v22/BitcoindV22RpcClient.html#listTransactions(account:String,count:Int,skip:Int,includeWatchOnly:Boolean):scala.concurrent.Future[Vector[org.bitcoins.commons.jsonmodels.bitcoind.ListTransactionsResult]]

(>def ::list-transactions-result (s/keys))

(>def ::fetch-block-by-height-result (s/keys))
