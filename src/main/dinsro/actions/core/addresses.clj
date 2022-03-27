(ns dinsro.actions.core.addresses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.tx :as a.c.tx]
   [dinsro.actions.nbxplorer :as a.nbxplorer]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.queries.core.addresses :as q.c.addresses]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [taoensso.timbre :as log]))

(>defn fetch!
  [id]
  [::m.c.addresses/id => any?]
  (if-let [address (q.c.addresses/read-record id)]
    (doseq [node-id (q.c.nodes/index-ids)]
      (log/infof "Fetching address: %s" address)
      (let [response (a.nbxplorer/get-transactions-for-address (::m.c.addresses/address address))]
        (doseq [transaction (:transactions (:confirmedTransactions response))]
          (let [{:keys [blockHash height transactionId]} transaction]
            (a.c.tx/register-tx node-id blockHash height transactionId))))
      nil)
    (do
      (log/error "no address")
      {:status  :failed
       :message "No address"})))
