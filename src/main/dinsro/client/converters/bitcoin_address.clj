(ns dinsro.client.converters.bitcoin-address
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.client.converters.script-pub-key :as cc.script-pub-key]
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.protocol.BitcoinAddress))

;; https://bitcoin-s.org/api/org/bitcoins/core/protocol/BitcoinAddress.html

(>def ::hash any?)
(>def ::is-standard? boolean?)
(>def ::network-parameters any?)
(>def ::script-pub-key ::cc.script-pub-key/record)
(>def ::value any?)

(>def ::record (s/keys :req [::hash ::is-standard? ::network-parameters ::script-pub-key ::value]))

(>defn BitcoinAddress->record
  [this]
  [(ds/instance? BitcoinAddress) => ::record]
  (let [record {::hash               (some-> this .hash)
                ::is-standard?       (some-> this .isStandard)
                ::network-parameters (some-> this .networkParameters)
                ::script-pub-key     (some-> this .scriptPubKey)
                ::value              (some-> this .value)}]
    (log/info :BitcoinAddress->record/finished {:record record})
    record))

(extend-type BitcoinAddress
  cs/Recordable
  (->record [this] (BitcoinAddress->record this)))
