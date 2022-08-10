(ns dinsro.client.converters.transaction-output
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.currency-unit :as cc.currency-unit]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.transaction.TransactionOutput))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/transaction/TransactionOutput.html

(>def ::script-pub-key string?)
(>def ::value ::cc.currency-unit/record)

(>def ::record (s/keys :req [::script-pub-key ::value]))

(>defn TransactionOutput->record
  [this]
  [(ds/instance? TransactionOutput) => ::record]
  (let [record {::script-pub-key (some-> this .scriptPubKey .hex)
                ::value          (some-> this .value cs/->record)}]
    (log/finer :TransactionOutput->record/finished {:record record})
    record))

(extend-type TransactionOutput
  cs/Recordable
  (->record [this] (TransactionOutput->record this)))
