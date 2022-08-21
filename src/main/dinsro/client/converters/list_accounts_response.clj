(ns dinsro.client.converters.list-accounts-response
  (:require
   [dinsro.client.scala :as cs])
  (:import
   walletrpc.ListAccountsResponse))

(defn ->response
  "https://bitcoin-s.org/api/walletrpc/ListAccountsResponse.html"
  ([]
   (->response (cs/empty-seq)))
  ([accounts]
   (->response accounts (cs/empty-unknown-field-set)))
  ([accounts unknown-fields]
   (ListAccountsResponse. accounts unknown-fields)))

(defn ListAccountsResponse->record
  [this]
  (let [accounts (some-> this
                         .accounts
                         cs/vector->vec
                         (some->> (map cs/->record)))]
    {::accounts accounts
     ::unknown-fields []}))
