(ns dinsro.client.converters.get-raw-transaction-vin
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.client.converters.get-raw-transaction-script-sig :as cc.get-raw-transaction-script-sig]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetRawTransactionVin))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetRawTransactionVin.html

(>def ::product-element-names any?)
(>def ::script-sig (? ::cc.get-raw-transaction-script-sig/record))
(>def ::sequence (? number?))
(>def ::tx-id (? string?))
(>def ::tx-in-witness (s/coll-of string?))
(>def ::vout (? number?))

(>def ::record
  (s/keys
   :req
   [;; ::product-element-names
    ::script-sig
    ::sequence
    ::tx-id
    ::tx-in-witness
    ::vout]))

(>defn GetRawTransactionVin->record
  [this]
  [(ds/instance? GetRawTransactionVin) => ::record]
  (let [tx-in-witness (some-> this .txinwitness cs/get-or-nil cs/vector->vec)
        record {::product-element-names (some-> this .productElementNames .toVector cs/vector->vec)
                ::script-sig            (some-> this .scriptSig cs/get-or-nil cs/->record)
                ::sequence              (some-> this .sequence cs/get-or-nil .toLong)
                ::tx-id                 (some-> this .txid cs/get-or-nil cs/->record)
                ::tx-in-witness         tx-in-witness
                ::vout                  (some-> this .vout cs/get-or-nil)}]
    (log/finer :GetRawTransactionVin->record/finished {:record record})
    record))

(extend-type GetRawTransactionVin
  cs/Recordable
  (->record [this] (GetRawTransactionVin->record this)))
