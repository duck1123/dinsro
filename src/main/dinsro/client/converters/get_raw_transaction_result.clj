(ns dinsro.client.converters.get-raw-transaction-result
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.witness-transaction :as cc.witness-transaction]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetRawTransactionResult))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetRawTransactionResult.html

(>def ::block-hash string?)

(>def ::block-time number?)

(>def ::confirmations number?)

(>def ::hash string?)

(>def ::hex (s/or :record ::cc.witness-transaction/record :nil nil?))

(>def ::in-active-blockchain? boolean?)

(>def ::locktime number?)

(>def ::size number?)

(>def ::time number?)

(>def ::tx-id string?)

(>def ::version number?)

(>def ::vin any?)

(>def ::vout any?)

(>def ::vsize number?)

(>def ::record
  (s/keys
   :req
   [::block-hash
    ::block-time
    ::confirmations
    ::hash
    ::hex
    ::in-active-blockchain?
    ::locktime
    ::size
    ::time
    ::tx-id
    ::version
    ::vin
    ::vout
    ::vsize]))

(>defn GetRawTransactionResult->record
  [^GetRawTransactionResult this]
  [(ds/instance? GetRawTransactionResult) => ::record]
  (log/info :GetRawTransactionResult->record/starting {:this this
                                                       :hash (some-> this .hash)})
  (let [vin    (if-let [inputs (some-> this .vin cs/vector->vec)]
                 (doall (map cs/->record inputs))
                 [])
        vout   (if-let [outputs (some-> this .vout cs/vector->vec)]
                 (doall (map cs/->record outputs))
                 [])
        hex    (some-> this .hex cs/->record)
        record {::block-hash            (some-> this .blockhash cs/get-or-nil .hex)
                ::block-time            (some-> this .blocktime cs/get-or-nil .toLong)
                ::confirmations         (some-> this .confirmations cs/get-or-nil)
                ::hash                  (some-> this .hash .hex)
                ::hex                   hex
                ::in-active-blockchain? (boolean (some-> this .in_active_blockchain cs/get-or-nil))
                ::locktime              (some-> this .locktime .toLong)
                ::size                  (some-> this .size)
                ::time                  (some-> this .time cs/get-or-nil .toLong)
                ::tx-id                 (some-> this .txid .hex)
                ::version               (some-> this .version)
                ::vin                   vin
                ::vout                  vout
                ::vsize                 (some-> this .vsize)}]
    (log/finer :GetRawTransactionResult->record {:record record})
    record))

(extend-type GetRawTransactionResult
  cs/Recordable
  (->record [this] (GetRawTransactionResult->record this)))
