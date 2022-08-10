(ns dinsro.client.converters.rpc-script-pub-key
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.bitcoin-address :as cc.bitcoin-address]
   [dinsro.client.converters.script-type :as cc.script-type]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.RpcScriptPubKey))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/RpcScriptPubKey.html

(>def ::asm string?)
(>def ::hex string?)
(>def ::addresses (s/coll-of ::cc.bitcoin-address/record))
(>def ::script-type ::cc.script-type/record)

(>def ::record (s/keys :req [::addresses ::asm ::hex ::script-type]))

(>defn RpcScriptPubKey->record
  [this]
  [(ds/instance? RpcScriptPubKey) => ::record]
  (let [addresses []
        record    {::addresses   addresses
                   ::asm         (some-> this .asm)
                   ::hex         (some-> this .hex)
                   ::script-type (some-> this .scriptType cs/->record)}]
    (log/finer :RpcScriptPubKey->record/finished {:record record})
    record))

(extend-type RpcScriptPubKey
  cs/Recordable
  (->record [this] (RpcScriptPubKey->record this)))
