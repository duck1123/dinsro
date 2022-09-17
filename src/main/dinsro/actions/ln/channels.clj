(ns dinsro.actions.ln.channels
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.channels :as q.ln.channels]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(>defn update-channel!
  [node data]
  [::m.ln.nodes/item ::m.ln.channels/nodeless-params => any?]
  (let [{::m.ln.nodes/keys [id]}               node
        {::m.ln.channels/keys [channel-point]} data
        params                                 (assoc data ::m.ln.channels/node id)]
    (if-let [channel-id (q.ln.channels/find-channel id channel-point)]
      (do
        (log/info :update-channel!/has-channel {:channel-id channel-id})
        (if-let [channel (q.ln.channels/read-record channel-id)]
          (do
            (log/info :update-channel!/found {})
            (let [params (merge channel params)]
              (q.ln.channels/update! params)))
          (throw (RuntimeException. "Can't find channel"))))
      (do
        (log/error :update-channel!/no-channel {})
        (q.ln.channels/create-record params)))))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (throw (RuntimeException. "Not implemented")))

(>defn fetch-channels
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (let [client (a.ln.nodes/get-client node)]
    (c.lnd-s/list-channels client)))

(>defn fetch-channels!
  [node-id]
  [::m.ln.nodes/id => (? any?)]
  (log/info :fetch-channels!/starting {:node-id node-id})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (if-let [ch (fetch-channels node)]
      (do
        (async/go
          (let [data               (async/<! ch)
                {:keys [channels]} data]
            (doseq [channel channels]
              (log/info :fetch-channels!/processing-channel {:channel channel})
              (let [params (m.ln.channels/prepare-params channel)]
                (try
                  (update-channel! node params)
                  (catch Exception ex
                    (log/error :fetch-channels!/update-failed {:ex ex})))))))
        ch)
      (do
        (log/error :fetch-channels!/channel-error {})
        nil))
    (do
      (log/error :fetch-channels!/no-node {})
      nil)))
