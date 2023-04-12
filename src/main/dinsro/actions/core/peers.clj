(ns dinsro.actions.core.peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [lambdaisland.glogc :as log]))

;; returns a list of peer info

(>defn get-peer-info
  "Fetch peer info for node"
  [node]
  [::m.c.nodes/item => (s/coll-of :dinsro.client.converters.peer-post-v21/record)]
  (let [client (a.c.node-base/get-client node)]
    (c.bitcoin-s/get-peer-info client)))

(defn has-peer?
  [node address]
  (log/info :has-peer?/starting {:node node :address address})
  (let [peer-info (get-peer-info node)]
    (log/trace :has-peer?/got-info {:peer-info peer-info})
    (let [hosts (map (fn [x] (:addr (:network-info x))) peer-info)]
      (log/trace :has-peer?/got-info {:hosts hosts})
      (some (fn [x] (= x address)) hosts))))

(>defn update-peer!
  [node-id peer]
  [::m.c.peers/node :dinsro.client.converters.peer-post-v21/record => any?]
  (log/info :update-peer!/starting {:node-id node-id :peer peer})
  (if-let [peer-index (:id peer)]
    (do
      (log/trace :update-peer!/got-index {:peer-index peer-index :node-id node-id :peer peer})
      (if-let [existing-peer (q.c.peers/find-by-node-and-peer-id node-id peer-index)]
        (let [peer-id (::m.c.peers/id existing-peer)]
          (log/trace :update-peer!/record-exists
                     {:node-id    node-id
                      :peer-index peer-index
                      :peer-id    peer-id})
          peer-id)
        (do
          (log/trace :update-peer!/record-not-exists {:node-id node-id :peer-index peer-index})
          (let [params (assoc peer ::m.c.peers/node node-id)
                params (m.c.peers/prepare-params params)]
            (log/trace :update-peer!/params-prepared {:params params})
            (q.c.peers/create-record params)))))
    (do
      (log/error :update-peer!/peer-index-missing {:node-id node-id :peer peer})
      (throw (ex-info "Failed to find peer id" {})))))

(>defn fetch-peers!
  "Fetch and update peers for node"
  [node]
  [::m.c.nodes/item => nil?]
  (let [node-id (::m.c.nodes/id node)]
    (log/info :fetch-peers!/starting {:node-id node-id})
    (let [client  (a.c.node-base/get-client node)]
      (doseq [peer (c.bitcoin-s/get-peer-info client)]
        (update-peer! node-id peer)))))

;; returns :passed and :result (BoxedUnit)
(>defn add-peer!
  [node address]
  [::m.c.nodes/item string? => any?]
  (log/info :add-peer!/starting {:node-id (::m.c.nodes/id node) :address address})
  (let [client   (a.c.node-base/get-client node)
        response (c.bitcoin-s/add-node client address)]
    (log/trace :add-peer!/finished {:response response})
    response))

(>defn create!
  "Create a new peer connection for this node"
  [{addr    ::m.c.peers/addr
    node-id ::m.c.peers/node}]
  [::m.c.peers/params => any?]
  (log/info :create!/starting {:node-id node-id :addr addr})
  (if-let [node (q.c.nodes/read-record node-id)]
    (do
      (add-peer! node addr)
      (fetch-peers! node))
    (throw (ex-info (str "Failed to find node: " node-id) {}))))

(>defn delete!
  "Remove node and delete record"
  [{peer-id ::m.c.peers/id}]
  [::m.c.peers/item => any?]
  (if-let [peer (q.c.peers/read-record peer-id)]
    (let [{node-id ::m.c.peers/node
           addr    ::m.c.peers/addr} peer]
      (log/info :delete!/starting {:node-id node-id :peer-id peer-id})
      (if-let [node (q.c.nodes/read-record node-id)]
        (let [client (a.c.node-base/get-client node)]
          (c.bitcoin-s/disconnect-node client addr)
          (q.c.peers/delete! peer-id))
        (do
          (log/warn :delete!/node-not-found {:peer-id peer-id :node-id :node-id})
          nil)))
    (do
      (log/warn :delete!/peer-not-found {:peer-id peer-id})
      nil)))
