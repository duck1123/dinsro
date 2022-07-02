(ns dinsro.client.converters.get-block-result
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockResult))

(>def ::bits int?)
(>def ::chainwork string?)
(>def ::confirmations int?)
(>def ::difficulty number?)
(>def ::hash string?)
(>def ::height int?)
(>def ::median-time int?)
(>def ::merkle-root string?)
(>def ::next-block-hash (s/or :hash string? :no-hash nil?))

(>def ::record
      (s/keys :req-un
              [::bits
               ::chainwork
               ::confirmations
               ::difficulty
               ::hash
               ::height
               ::median-time
               ::merkle-root
               ::next-block-hash]))

(>defn GetBlockResult->record
  [^GetBlockResult this]
  [(ds/instance? GetBlockResult) => ::record]
  {:bits                (some-> this .bits .toInt)
   :chainwork           (.chainwork this)
   :confirmations       (.confirmations this)
   :difficulty          (.difficulty this)
   :hash                (some-> this .hash .hex)
   :height              (.height this)
   :median-time         (some-> this .mediantime .toInt)
   :merkle-root         (.merkleroot this)
   :next-block-hash     (some-> this .nextblockhash .orNull)
   :nonce               (some-> this .nonce .toInt)
   :previous-block-hash (some-> this .nextblockhash .orNull)
   :size                (.size this)
   :stripped-size       (.strippedsize this)
   :time                (some-> this .time .toInt)
   :tx                  (some-> this .tx cs/vector->vec)
   :version             (.version this)
   :version-hex         (some-> this .versionHex .toInt)
   :weight              (.weight this)})

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockResult.html

(extend-type GetBlockResult
  cs/Recordable
  (->record [this] (GetBlockResult->record this)))
