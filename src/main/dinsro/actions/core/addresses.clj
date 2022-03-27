(ns dinsro.actions.core.addresses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.tx :as a.core-tx]
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.model.core.addresses :as m.core-addresses]
   [dinsro.queries.core.addresses :as q.core-addresses]
   [dinsro.queries.core.nodes :as q.core-nodes]
   [taoensso.timbre :as log]))

(>defn fetch!
  [id]
  [::m.core-addresses/id => any?]
  (if-let [address (q.core-addresses/read-record id)]
    (doseq [node-id (q.core-nodes/index-ids)]
      (log/infof "Fetching address: %s" address)
      (let [response (a.nbxplorer/get-transactions-for-address (::m.core-addresses/address address))]
        (doseq [transaction (:transactions (:confirmedTransactions response))]
          (let [{:keys [blockHash height transactionId]} transaction]
            (a.core-tx/register-tx node-id blockHash height transactionId))))
      nil)
    (do
      (log/error "no address")
      {:status  :failed
       :message "No address"})))
