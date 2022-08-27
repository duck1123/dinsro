(ns dinsro.client.converters.get-block-result
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def
                                            #?(:clj >defn)
                                            #?(:clj =>)]]
   #?(:clj [dinsro.client.scala :as cs])
   #?(:clj [dinsro.specs :as ds])
   #?(:clj [lambdaisland.glogc :as log]))
  #?(:clj
     (:import
      org.bitcoins.commons.jsonmodels.bitcoind.GetBlockResult)))

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
  (s/keys :req
          [::bits
           ::chainwork
           ::confirmations
           ::difficulty
           ::hash
           ::height
           ::median-time
           ::merkle-root
           ::next-block-hash]))

#?(:clj
   (>defn GetBlockResult->record
     [^GetBlockResult this]
     [(ds/instance? GetBlockResult) => ::record]
     (let [txes   (some-> this .tx cs/vector->vec (some->> (map cs/->record)))
           record {::bits                (some-> this .bits .toLong)
                   ::chainwork           (.chainwork this)
                   ::confirmations       (.confirmations this)
                   ::difficulty          (some-> this .difficulty .toLong)
                   ::hash                (some-> this .hash .hex)
                   ::height              (.height this)
                   ::median-time         (some-> this .mediantime .toLong)
                   ::merkle-root         (some-> this .merkleroot .hex)
                   ::next-block-hash     (some-> this .nextblockhash cs/get-or-nil cs/->record)
                   ::nonce               (some-> this .nonce .toInt)
                   ::previous-block-hash (some-> this .previousblockhash cs/get-or-nil cs/->record)
                   ::size                (.size this)
                   ::stripped-size       (.strippedsize this)
                   ::time                (some-> this .time .toLong)
                   ::tx                  txes
                   ::version             (.version this)
                   ::version-hex         (some-> this .versionHex .hex)
                   ::weight              (.weight this)}]
       (log/finer :GetBlockResult->record/finished {:record record})
       record)))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockResult.html

#?(:clj
   (extend-type GetBlockResult
     cs/Recordable
     (->record [this] (GetBlockResult->record this))))
