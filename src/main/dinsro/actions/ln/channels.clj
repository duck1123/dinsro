(ns dinsro.actions.ln.channels
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.ln.nodes :as a.ln-nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.channels :as m.ln-channels]
   [dinsro.model.ln.nodes :as m.ln-nodes]
   [dinsro.queries.ln.channels :as q.ln-channels]
   [dinsro.queries.ln.nodes :as q.ln-nodes]
   [dinsro.specs :as ds]
   [taoensso.timbre :as log]))

(>defn fetch-channels
  [node]
  [::m.ln-nodes/item => ds/channel?]
  (let [ch      (async/chan)
        request (c.lnd/->list-channels-request)]
    (with-open [client (a.ln-nodes/get-client node)]
      (.listChannels client request (c.lnd/ch-observer ch)))
    ch))

(>defn update-channel!
  [node data]
  [::m.ln-nodes/item ::m.ln-channels/nodeless-params => any?]
  (let [{::m.ln-nodes/keys [id]}               node
        {::m.ln-channels/keys [channel-point]} data
        params                                 (assoc data ::m.ln-channels/node id)]
    (if-let [channel-id (q.ln-channels/find-channel id channel-point)]
      (do
        (log/infof "has channel: %s" channel-id)
        (if-let [channel (q.ln-channels/read-record channel-id)]
          (do
            (log/info "found")
            (let [params (merge channel params)]
              (q.ln-channels/update! params)))
          (throw (RuntimeException. "Can't find channel"))))
      (do
        (log/error "no channel")
        (q.ln-channels/create-record params)))))

(>defn fetch-channels!
  [id]
  [::m.ln-nodes/id => (? any?)]
  (log/infof "Fetching Channels - %s" id)
  (if-let [node (q.ln-nodes/read-record id)]
    (if-let [ch (fetch-channels node)]
      (do
        (async/go
          (let [data               (async/<! ch)
                {:keys [channels]} data]
            (doseq [channel channels]
              (log/infof "channel: %s" channel)
              (let [params (m.ln-channels/prepare-params channel)]
                (try
                  (update-channel! node params)
                  (catch Exception ex
                    (log/error "Failed to update" ex)))))))
        ch)
      (do
        (log/error "channel error")
        nil))
    (do
      (log/error "No Node")
      nil)))

(comment
  (q.ln-channels/index-ids)
  (first (q.ln-channels/index-records))

  (q.ln-channels/index-records)

  (map q.ln-channels/delete! (q.ln-channels/index-ids))

  nil)
