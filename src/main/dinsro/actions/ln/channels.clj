(ns dinsro.actions.ln.channels
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.channels :as q.ln.channels]
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
