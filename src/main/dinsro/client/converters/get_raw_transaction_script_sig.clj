(ns dinsro.client.converters.get-raw-transaction-script-sig
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.script-signature :as cc.script-signature]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetRawTransactionScriptSig))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetRawTransactionScriptSig.html


(>def ::asm string?)
(>def ::hex ::cc.script-signature/record)
(>def ::product-element-names (s/coll-of string?))

(>def ::record (s/keys :req [::asm ::hex ::product-element-names]))

(>defn GetRawTransactionScriptSig->record
  [this]
  [(ds/instance? GetRawTransactionScriptSig) => ::record]
  (let [record {::asm                   (some-> this .asm)
                ::hex                   (some-> this .hex cs/->record)
                ::product-element-names (some-> this .productElementNames .toVector cs/vector->vec)}]
    (log/info :GetRawTransactionScriptSig->record/finished {:record record})
    record))

(extend-type GetRawTransactionScriptSig
  cs/Recordable
  (->record [this] (GetRawTransactionScriptSig->record this)))
