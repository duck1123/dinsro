(ns dinsro.client.converters.ext-private-key
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def
                                            >defn =>]]

   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.crypto.ExtPrivateKey
   org.bitcoins.core.hd.BIP32Path))

(>def ::chain-code string?)
(>def ::child-num number?)
(>def ::depth number?)
(>def ::fingerprint string?)
(>def ::key string?)
(>def ::version string?)

(>def ::record
      (s/keys
       :req-un [::chain-code ::depth ::fingerprint ::key ::version]))

(defn ->path
  ([]
   (->path "m"))
  ([path-string]
   (BIP32Path/fromString path-string)))

(defn ->obj
  ([version]
   (->obj version (cs/none)))
  ([version seed-opt]
   (->obj version seed-opt (->path)))
  ([version seed-opt bip32-path]
   (ExtPrivateKey/apply version seed-opt bip32-path)))

;; https://bitcoin-s.org/api/org/bitcoins/core/crypto/ExtPrivateKey.html

(>defn ExtPrivateKey->record
  [this]
  [(ds/instance? ExtPrivateKey) => ::record]
  (let [chain-code  (some-> this .chainCode .hex)
        child-num   (some-> this .childNum .toLong)
        depth       (some-> this .depth .toLong)
        fingerprint (some-> this .fingerprint .toHex)
        key         (some-> this .key .hex)
        version     (some-> this .version .hex)
        record      {:chain-code  chain-code
                     :child-num   child-num
                     :depth       depth
                     :fingerprint fingerprint
                     :key         key
                     :version     version}]
    (log/info :ExtPrivateKey->record/finished {:record record})
    record))

(comment

  (.-empty BIP32Path)

  (BIP32Path/fromString "m")

  nil)
