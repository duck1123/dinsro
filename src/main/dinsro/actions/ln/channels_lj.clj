(ns dinsro.actions.ln.channels-lj
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.ln.channels :as a.ln.channels]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(>defn fetch-channels
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (let [ch      (async/chan)
        request (c.lnd/->list-channels-request)]
    (with-open [client (a.ln.nodes-lj/get-client node)]
      (.listChannels client request (c.lnd/ch-observer ch)))
    ch))

(>defn fetch-channels!
  [id]
  [::m.ln.nodes/id => (? any?)]
  (log/info :fetch-channels!/starting {:id id})
  (if-let [node (q.ln.nodes/read-record id)]
    (if-let [ch (fetch-channels node)]
      (do
        (async/go
          (let [data               (async/<! ch)
                {:keys [channels]} data]
            (doseq [channel channels]
              (log/info :fetch-channels!/processing-channel {:channel channel})
              (let [params (m.ln.channels/prepare-params channel)]
                (try
                  (a.ln.channels/update-channel! node params)
                  (catch Exception ex
                    (log/error :fetch-channels!/update-failed {:ex ex})))))))
        ch)
      (do
        (log/error :fetch-channels!/channel-error {})
        nil))
    (do
      (log/error :fetch-channels!/no-node {})
      nil)))
