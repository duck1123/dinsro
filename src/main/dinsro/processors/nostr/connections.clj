(ns dinsro.processors.nostr.connections
  (:require
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.responses.nostr.connections :as r.n.connections]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/connections.clj]]

(def model-key ::m.n.connections/id)

(defn connect!
  [props]
  (log/info :connect!/starting {:props props})
  (throw (ex-info "Not Implemented" {})))

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.n.connections/delete! id)
    {::mu/status :ok
     ::r.n.connections/deleted-records (m.n.connections/idents [id])}))

(defn disconnect!
  [props]
  (log/info :disconnect!/starting {:props props})
  (let [id (model-key props)]
    (a.n.connections/disconnect! id)
    {::mu/status :ok}))

(defn toggle!
  [_env props]
  (let [relay-id (::m.n.relays/id props)]
    (log/info :do-toggle!/starting {:relay-id relay-id})
    (if-let [connection-id (q.n.connections/find-connected-by-relay relay-id)]
      (do
        (log/info :do-toggle!/found {:connection-id connection-id})
        {::mu/status :ok})
      (let [connection-id (a.n.connections/register-connection! relay-id)]
        (a.n.connections/start! connection-id)
        {::mu/status :ok}))))
