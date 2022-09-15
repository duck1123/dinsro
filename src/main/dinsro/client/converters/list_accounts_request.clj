(ns dinsro.client.converters.list-accounts-request
  (:require
   [dinsro.client.scala :as cs])
  (:import
   walletrpc.ListAccountsRequest
   walletrpc.AddressType$Unrecognized))

(defn ->obj
  "https://bitcoin-s.org/api/walletrpc/ListAccountsRequest.html"
  ([]
   (->obj ""))
  ([name]
   (->obj name (AddressType$Unrecognized. 0)))
  ([name address-type]
   (let [unknown-fields (cs/empty-unknown-field-set)]
     (->obj name address-type unknown-fields)))
  ([name address-type unknown-fields]
   (ListAccountsRequest. name address-type unknown-fields)))
