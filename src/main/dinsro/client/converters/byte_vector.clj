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
    (log/trace :ByteVector->record/finished {:record record})
    record))

(defn ->obj
  ([hex] (->obj hex (ByteVector/fromHexDescriptive$default$2)))
  ([hex alphabet]
   (ByteVector/fromHex hex alphabet)))

(extend-type ByteVector
  cs/Recordable
  (->record [this] (ByteVector->record this)))
