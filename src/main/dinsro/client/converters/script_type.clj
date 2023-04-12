(ns dinsro.client.converters.script-type
  (:require
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.script.ScriptType))

;; https://bitcoin-s.org/api/org/bitcoins/core/script/ScriptType.html

(>def ::record string?)

(>defn ScriptType->record
  [this]
  [(ds/instance? ScriptType) => ::record]
  (let [record (str this)]
    (log/trace :ScriptType->record/finished {:record record})
    record))

(extend-type ScriptType
  cs/Recordable
  (->record [this] (ScriptType->record this)))
