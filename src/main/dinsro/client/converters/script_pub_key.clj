(ns dinsro.client.converters.script-pub-key
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.script.ScriptPubKey))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/script/ScriptPubKey.html


(>def ::hex string?)

(>def ::record
      (s/keys
       :req
       [::hex]))

(>defn ScriptPubKey->record
  [this]
  [(ds/instance? ScriptPubKey) => ::record]
  (let [record {::hex (some-> this .hex)}]
    (log/info :ScriptPubKey->record/finished {:record record})
    record))

(extend-type ScriptPubKey
  cs/Recordable
  (->record [this] (ScriptPubKey->record this)))
