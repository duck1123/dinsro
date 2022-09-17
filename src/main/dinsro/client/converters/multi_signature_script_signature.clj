(ns dinsro.client.converters.multi-signature-script-signature
  (:require
   [dinsro.client.scala :as cs])
  (:import
   org.bitcoins.core.protocol.script.MultiSignatureScriptSignature))

(extend-type MultiSignatureScriptSignature
  cs/Recordable
  (->record [this] (.hex this)))
