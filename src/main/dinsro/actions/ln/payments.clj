(ns dinsro.actions.ln.payments
  (:require
   [clojure.core.async :as async :refer [<! <!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payments :as q.ln.payments]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(>defn fetch-payments
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (a.ln.nodes/get-client node)]
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

(comment
  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (update-payments node)

  (fetch-payments node)

  (q.ln.payments/index-records)
  (q.ln.payments/index-ids)

  (map q.ln.payments/delete!
       (q.ln.payments/index-ids))

  (let [ch (async/chan 1)]
    (async/pipeline
     1
     ch
     (map (fn [z] z))
     (fetch-payments node)
     true
     (fn [error] (println "ahhh: " (.getMessage error)))))

  (<!! (fetch-payments node))

  (let [chan (fetch-payments node)]
    (async/go-loop []
      (let [a (<! chan)] (when a a (recur)))))

  nil)
