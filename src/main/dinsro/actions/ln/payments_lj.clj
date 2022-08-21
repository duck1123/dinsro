(ns dinsro.actions.ln.payments-lj
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payments :as q.ln.payments]
   [taoensso.timbre :as log]))

(>defn fetch-payments
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (c.lnd/list-payments client)))

(>defn update-payments
  [node]
  [::m.ln.nodes/item => any?]
  (let [node-id           (::m.ln.nodes/id node)
        {:keys [payments]} (log/spy :info (<!! (fetch-payments node)))]
    (doseq [params payments]
      (let [params (assoc params ::m.ln.payments/node node-id)
            params (m.ln.payments/prepare-params params)]
        (q.ln.payments/create-record (log/spy :info params))))))

(defn fetch!
  [node-id]
  (when-let [node (q.ln.nodes/read-record node-id)]
    (update-payments node)))
