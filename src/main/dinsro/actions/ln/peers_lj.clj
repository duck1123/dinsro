(ns dinsro.actions.ln.peers-lj
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(>defn fetch-peers
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (let [ch (async/chan)]
    (with-open [client (a.ln.nodes-lj/get-client node)]
      (.listPeers client true (c.lnd/ch-observer ch)))
    ch))

(>defn fetch-peers!
  [id]
  [::m.ln.nodes/id => (? any?)]
  (log/info :fetch-peers!/starting {:id id})
  (if-let [node (q.ln.nodes/read-record id)]
    (if-let [ch (fetch-peers node)]
      (let [data            (async/<!! ch)
            {:keys [peers]} data]
        (doseq [peer peers]
          (a.ln.peers/handle-fetched-peer node peer))
        ch)
      (do
        (log/error :fetch-peers!/no-peer {})
        nil))
    (do
      (log/error :fetch-peers!/no-node {})
      nil)))
