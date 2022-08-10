(ns dinsro.client.converters.transaction-out-point
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.transaction.TransactionOutPoint))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/transaction/TransactionOutPoint.html


;; little endian


(>def ::tx-id string?)

(>def ::vout number?)

(>def ::record (s/keys :req [::tx-id ::vout]))

(>defn TransactionOutPoint->record
  [this]
  [(ds/instance? TransactionOutPoint) => ::record]
  (let [record {::tx-id (some-> this .txId .hex)
                ::vout  (some-> this .vout .toLong)}]
    (log/finer :TransactionOutPoint->record/finished {:record record})
    record))

(extend-type TransactionOutPoint
  cs/Recordable
  (->record [this] (TransactionOutPoint->record this)))
