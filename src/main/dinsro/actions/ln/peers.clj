(ns dinsro.actions.ln.peers
  (:refer-clojure :exclude [next])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [lambdaisland.glogc :as log]))

(defn create-peer-record!
  [data]
  (log/info :create-peer-record!/starting {:data data})
  (q.ln.peers/create-record data))

(>defn update-peer!
  [node data]
  [::m.ln.nodes/item ::m.ln.peers/params => any?]
  (log/info :update-peer!/starting {:node node :data data})
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

(>defn handle-fetched-peer
  [node peer]
  [::m.ln.nodes/item any? => any?]
  (log/info :handle-fetched-peer/starting {:node node :peer peer})
  (try
    (let [params   (set/rename-keys peer m.ln.peers/rename-map)
          response (update-peer! node params)]
      (log/info :handle-fetched-peer/finished {:response response})
      response)
    (catch Exception ex
      (log/error :handle-fetched-peer/failed {:ex ex}))))

(defn delete!
  "Handler for delete peer mutation"
  [props]
  (log/info :delete!/starting {:props props})
  (let [{peer-id ::m.ln.peers/id} props]
    (q.ln.peers/delete peer-id)))

(>defn create-peer!
  [node host pubkey]
  [::m.ln.nodes/item string? string? => any?]
  (log/info :create-peer!/starting {:pubkey pubkey :host host :node-id (::m.ln.nodes/id node)})
  (let [client (a.ln.nodes/get-client node)]
    (c.lnd-s/connect-peer! client host pubkey)))

(defn create!
  "Handler for new peer submit button"
  [{::m.ln.peers/keys [address]
    node-id           ::m.ln.peers/node
    :as               props}]
  (log/info :create!/starting {:props props})
  (let [node   (q.ln.nodes/read-record node-id)
        host   address
        pubkey nil]
    (create-peer! node host pubkey)))

(>def ::peer-response (s/keys))

(>defn make-peer*
  "Mutation handler for make-peer!"
  [node-id remote-node-id]
  [::m.ln.remote-nodes/node ::m.ln.remote-nodes/id => ::peer-response]
  (if-let [node (q.ln.nodes/read-record node-id)]
    (if-let [remote-node (q.ln.remote-nodes/read-record remote-node-id)]
      (let [{::m.ln.remote-nodes/keys [pubkey host]} remote-node]
        (if pubkey
          (let [response (create-peer! node host pubkey)]
            (log/info :make-peer*/finished {:response response})
            {:status (if (nil? response) :fail :ok)})
          {:status :fail :message "No pubkey"}))
      {:status :not-found :message "Failed to find remote node"})
    {:status :not-found :message "Failed to find node"}))

(>defn fetch-peers!
  [node-id]
  [::m.ln.nodes/id => (? any?)]
  (log/info :fetch-peers!/starting {:node-id node-id})
  #_(throw (RuntimeException. "Not implemented"))
  nil)
