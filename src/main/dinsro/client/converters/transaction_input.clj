(ns dinsro.client.converters.transaction-input
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.transaction.TransactionInput))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/transaction/TransactionInput.html

(>def ::previous-output any?)
(>def ::script-signature any?)
(>def ::sequence number?)

(>def ::record (s/keys :req [::previous-output ::script-signature ::sequence]))

(>defn TransactionInput->record
  [this]
  [(ds/instance? TransactionInput) => ::record]
  (let [record {::previous-output  (some-> this .previousOutput cs/->record)
                ::script-signature (some-> this .scriptSignature cs/->record)
                ::sequence         (some-> this .sequence .toLong)}]
    (log/trace :TransactionInput->record/finished {:record record})
    record))

(extend-type TransactionInput
  cs/Recordable
  (->record [this] (TransactionInput->record this)))
