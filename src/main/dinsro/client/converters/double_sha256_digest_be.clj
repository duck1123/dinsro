(ns dinsro.client.converters.double-sha256-digest-be
  (:require
   [dinsro.client.scala :as cs])
  (:import
   org.bitcoins.crypto.DoubleSha256DigestBE))

(defn DoubleSha256DigestBE->record
  [this]
  (.hex this))

;; https://bitcoin-s.org/api/org/bitcoins/crypto/DoubleSha256DigestBE.html

(extend-type DoubleSha256DigestBE
  cs/Recordable
  (->record [this] (DoubleSha256DigestBE->record this)))
