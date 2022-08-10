(ns dinsro.client.converters.script-token
  (:require
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds])
  (:import
   org.bitcoins.core.script.constant.ScriptToken))

;; https://bitcoin-s.org/api/org/bitcoins/core/script/constant/ScriptToken.html


(>def ::record string?)

(>defn ScriptToken->record
  [this]
  [(ds/instance? ScriptToken) => ::record]
  (.hex this))

(extend-type ScriptToken
  cs/Recordable
  (->record [this] (ScriptToken->record this)))
