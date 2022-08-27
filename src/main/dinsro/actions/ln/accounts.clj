(ns dinsro.actions.ln.accounts
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (throw (RuntimeException. "Not implemented")))

(defn fetch!
  [node]
  (let [node-id (::m.ln.nodes/id node)]
    (log/info :fetch!/starting {:node-id node-id})
    (let [client (a.ln.nodes/get-client node)
          response (c.lnd-s/list-wallet-accounts client)]
      (log/info :fetch!/finished {:response response})
      response)))

(defn do-fetch-accounts!
  [node-id]
  (log/info :do-fetch-accounts!/starting {:node-id node-id})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (do
      (log/info :do-fetch-accounts!/node-found {:node node})
      (fetch! node))
    (do
      (log/error :do-fetch-accounts!/node-not-found {:node-id node-id})
      {:status :fail})))
