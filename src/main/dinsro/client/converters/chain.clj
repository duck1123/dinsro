(ns dinsro.client.converters.chain
  (:require
   [dinsro.client.scala :as cs])
  (:import
   lnrpc.Chain))

(extend-type Chain
  cs/Recordable
  (->record [this]
    {:chain   (.chain this)
     :network (.network this)}))
