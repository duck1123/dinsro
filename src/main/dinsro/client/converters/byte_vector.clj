(ns dinsro.client.converters.byte-vector
  (:require
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   scodec.bits.ByteVector))

(>def ::record string?)

(>defn ByteVector->record
  [this]
  [(ds/instance? ByteVector) => ::record]
  (let [record (.toHex this)]
    (log/finer :ByteVector->record/finished {:record record})
    record))

(extend-type ByteVector
  cs/Recordable
  (->record [this] (ByteVector->record this)))