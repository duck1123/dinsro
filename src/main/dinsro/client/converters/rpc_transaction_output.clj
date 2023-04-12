(ns dinsro.client.converters.rpc-transaction-output
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.rpc-script-pub-key :as cc.rpc-script-pub-key]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.RpcTransactionOutput))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/RpcTransactionOutput.html

(>def ::n number?)
(>def ::script-pub-key ::cc.rpc-script-pub-key/record)

;; Bitcoins
(>def ::value any?)

(>def ::record (s/keys :req [::n ::script-pub-key ::value]))

(>defn RpcTransactionOutput->record
  [this]
  [(ds/instance? RpcTransactionOutput) => ::record]
  (let [record {::n              (some-> this .n)
                ::script-pub-key (some-> this .scriptPubKey cs/->record)
                ::value          (some-> this .value cs/->record)}]
    (log/trace :RpcTransactionOutput->record/finished {:record record})
    record))

(extend-type RpcTransactionOutput
  cs/Recordable
  (->record [this] (RpcTransactionOutput->record this)))
