(ns dinsro.client.converters.uint32
  (:require
   [dinsro.client.scala :as cs])
  (:import
   org.bitcoins.core.number.UInt32))

(extend-type UInt32
  cs/Recordable
  (->record [this] (.toLong this)))
