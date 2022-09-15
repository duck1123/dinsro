(ns dinsro.client.converters.address-type
  (:require
   [dinsro.client.scala :as cs])
  (:import
   walletrpc.AddressType))

(defn AddressType->record
  [this]
  {::name (some-> this .name)
   ::index (some-> this .index)})

(extend-type AddressType
  cs/Recordable
  (->record [this] (AddressType->record this)))
