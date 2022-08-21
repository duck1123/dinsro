(ns dinsro.actions.ln.peers
  (:refer-clojure :exclude [next])
  (:require
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [lambdaisland.glogc :as log]))

(defn create-peer-record!
  [data]
  (log/info :create-peer-record!/starting {:data data})
  (q.ln.peers/create-record data))

(>defn update-peer!
  [node data]
  [::m.ln.nodes/item ::m.ln.peers/params => any?]
  (let [{::m.ln.nodes/keys [id]}     node
        {::m.ln.peers/keys [pubkey]} data
        params                       (assoc data ::m.ln.peers/node id)]
    (if-let [peer-id (q.ln.peers/find-peer id pubkey)]
      (if-let [peer (q.ln.peers/read-record peer-id)]
        (do
          (log/info :update-peer!/starting {:peer peer :data data})
          (let [params (merge peer params)]
            (q.ln.peers/update! params))
          nil)
        (throw (RuntimeException. "Can't find peer")))
      (let [network-id (q.c.networks/find-by-chain-and-network "bitcoin" "regtest")]
        (log/error :update-peer!/no-peer {:network-id network-id :params params})
        (let [remote-node-id (a.ln.remote-nodes/register-node! network-id pubkey nil)
              params         (assoc params ::m.ln.peers/remote-node remote-node-id)]
          (create-peer-record! params))))))

(defn handle-fetched-peer
  [node peer]
  (try
    (let [params (set/rename-keys peer m.ln.peers/rename-map)]
      (update-peer! node params))
    (catch Exception ex
      (log/error "Failed to update" ex))))

(defn delete!
  "Handler for delete peer mutation"
  [props]
  (log/info :delete!/starting {:props props})
  (let [{peer-id ::m.ln.peers/id} props]
    (q.ln.peers/delete peer-id)))
