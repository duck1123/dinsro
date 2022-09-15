(ns dinsro.client.converters.account
  (:require
   [dinsro.client.scala :as cs])
  (:import
   walletrpc.Account))

(defn Account->record
  [this]
  (let [address-type           (some-> this .addressType cs/->record)
        derivation-path        (some-> this .derivationPath)
        extended-public-key    (some-> this .extendedPublicKey)
        master-key-fingerprint (some-> this .masterKeyFingerprint .toStringUtf8)]
    {::address-type           address-type
     ::derivation-path        derivation-path
     ::extended-public-key    extended-public-key
     ::master-key-fingerprint master-key-fingerprint}))

(extend-type Account
  cs/Recordable
  (->record [this] (Account->record this)))
