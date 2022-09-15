(ns dinsro.actions.ln.invoices-lj
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.invoices :as q.ln.invoices]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [lambdaisland.glogc :as log]))

(>defn fetch
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (<!! (c.lnd/list-invoices client))))

(defn add-invoice
  [data]
  (let [{{node-id ::m.ln.nodes/id} ::m.ln.invoices/node} data
        node                                             (q.ln.nodes/read-record node-id)
        client                                           (a.ln.nodes-lj/get-client node)]
    (log/info :add-invoice/starting {:data data})
    (let [{::m.ln.invoices/keys [value memo]} data]
      (<!! (c.lnd/add-invoice client value memo)))))

(>defn update!
  [node-id]
  [::m.ln.nodes/id => any?]
  (log/info :update!/starting {})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (doseq [params (:invoices (fetch node))]
      (let [{:keys [addIndex]} params]
        (if-let [old-invoice-id (q.ln.invoices/find-by-node-and-index node-id addIndex)]
          (if-let [old-invoice (q.ln.invoices/read-record old-invoice-id)]
            (let [params     (assoc params ::m.ln.invoices/node node-id)
                  params     (m.ln.invoices/prepare-params params)
                  new-params (merge old-invoice params)]
              (log/info :update!/invoice-found {:new-params new-params})
              (q.ln.invoices/update! new-params))
            (throw (RuntimeException. "Failed to find invoice")))
          (let [params (assoc params ::m.ln.invoices/node node-id)
                params (m.ln.invoices/prepare-params params)]
            (q.ln.invoices/create-record params)))))
    (throw (RuntimeException. "Failed to find node"))))