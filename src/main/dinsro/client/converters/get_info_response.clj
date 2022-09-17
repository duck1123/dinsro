(ns dinsro.client.converters.get-info-response
  (:require
   [dinsro.client.scala :as cs :refer [Recordable]])
  (:import
   lnrpc.GetInfoResponse))

(defn GetInfoResponse->record
  [this]
  {:alias                 (.alias this)
   :color                 (some-> this .color)
   :block-hash            (some->  this .blockHash)
   :block-height          (some-> this .blockHeight cs/->record)
   :chains                (map cs/->record (cs/vector->vec (.chains this)))
   :version               (.version this)
   :identity-pubkey       (some-> this .identityPubkey)
   :commit-hash           (.commitHash this)})

(extend-type GetInfoResponse
  Recordable
  (->record [this] (GetInfoResponse->record this)))
