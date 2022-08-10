(ns dinsro.client.converters.witness-transaction
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.client.converters.transaction-input :as cc.transaction-input]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.transaction.WitnessTransaction))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/transaction/Transaction.html

(>def ::bytes any?)
(>def ::inputs (s/coll-of ::cc.transaction-input/record))

(>def ::record (s/keys :req [::bytes ::inputs ::locktime ::outputs ::version ::weight]))

(>defn WitnessTransaction->record
  [this]
  [(ds/instance? WitnessTransaction) => ::record]
  (log/finer :WitnessTransaction->record/starting {:this this})
  (let [inputs  (if-let [inputs (some-> this .inputs cs/vector->vec)]
                  (doall (map cs/->record inputs))
                  [])
        outputs (if-let [outputs (some-> this .outputs cs/vector->vec)]
                  (doall (map cs/->record outputs))
                  [])
        record  {::bytes    (some-> this .bytes cs/->record)
                 ::inputs   inputs
                 ::locktime (some-> this .lockTime .toLong)
                 ::outputs  outputs
                 ::version  (some-> this .version .toLong)
                 ::weight   (some-> this .weight)}]
    (log/finer :WitnessTransaction->record/finished {:record record})
    record))

(extend-type WitnessTransaction
  cs/Recordable
  (->record [this] (WitnessTransaction->record this)))
