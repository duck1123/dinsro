(ns dinsro.actions.nostr.requests
  (:require
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(defn create-request
  [relay-id code]
  (if-let [request-id (q.n.requests/find-by-relay-and-code relay-id code)]
    (throw (RuntimeException. (str "request already exists - " request-id)))
    (let [request-id (q.n.requests/create-record
                      {::m.n.requests/relay relay-id
                       ::m.n.requests/code  code})]
      (log/info :create-request/finished {:request-id request-id})
      request-id)))

(defn register-request
  [relay-id code]
  (if-let [request-id (q.n.requests/find-by-relay-and-code relay-id code)]
    request-id
    (create-request relay-id code)))

(defn start!
  [request-id]
  (log/info :start!/starting {:request-id request-id})
  (let [relay-id (q.n.requests/find-relay request-id)]
    relay-id))

(defn do-stop!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (log/info :do-stop!/starting {:request-id request-id})
      {:status "ok"})
    (throw (RuntimeException. "No request id"))))

(comment

  (q.n.relays/index-ids)

  (def relay-id (q.n.relays/find-by-address "wss://relay.kronkltd.net"))
  relay-id
  (def code "adhoc1")

  (map q.n.requests/read-record (q.n.requests/find-by-relay relay-id))

  (ds/gen-key ::m.n.requests/item)

  (q.n.requests/initialize-queries!)

  (def request-id (register-request relay-id code))
  request-id
  (q.n.requests/read-record request-id)

  (q.n.requests/set-started request-id)
  (q.n.requests/set-stopped request-id)

  (q.n.relays/read-record (q.n.requests/find-relay request-id))

  (q.n.requests/delete-all!)

  (q.n.requests/read-record (first (q.n.requests/index-ids)))

  (some-> relay-id q.n.requests/find-relay q.n.relays/read-record)

  ds/date

  (ds/->inst)

  nil)
