(ns dinsro.client.converters.ec-digital-signature
  (:require
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.crypto.ECDigitalSignature))

;; https://bitcoin-s.org/api/org/bitcoins/crypto/ECDigitalSignature.html

(>def ::record string?)

(>defn ECDigitalSignature->record
  [this]
  [(ds/instance? ECDigitalSignature) => ::record]
  (let [record (some-> this .hex)]
    (log/info :ECDigitalSignature->record/finished {:record record})
    record))

(extend-type ECDigitalSignature
  cs/Recordable
  (->record [this] (ECDigitalSignature->record this)))
