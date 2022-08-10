(ns dinsro.client.converters.script-signature
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.ec-digital-signature :as cc.ec-digital-signature]
   [dinsro.client.converters.script-token :as cc.script-token]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.script.ScriptSignature))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/script/ScriptSignature.html


(>def ::asm (s/coll-of ::cc.script-token/record))
(>def ::signatures (s/coll-of ::cc.ec-digital-signature/record))

(>def ::record (s/keys :req [::asm ::signatures]))

(>defn ScriptSignature->record
  [this]
  [(ds/instance? ScriptSignature) => ::record]
  (let [signatures (if-let [digital-signatures (some-> this .signatures .toVector cs/vector->vec)]
                     (doall (map cs/->record digital-signatures))
                     [])
        asm        (if-let [constants (some-> this .asm .toVector cs/vector->vec)]
                     (doall (map cs/->record constants))
                     [])
        record     {::asm        asm
                    ::signatures signatures}]
    (log/finer :ScriptSignature->record/finished {:record record})
    record))

(extend-type ScriptSignature
  cs/Recordable
  (->record [this] (ScriptSignature->record this)))
