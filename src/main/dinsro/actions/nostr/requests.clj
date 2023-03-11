(ns dinsro.actions.nostr.requests
  (:require
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(defn create-request
  [relay-id code]
  (let [start-time (ds/->inst)]
    (q.n.requests/create-record
     {::m.n.requests/relay      relay-id
      ::m.n.requests/start-time start-time
      ::m.n.requests/end-time   nil
      ::m.n.requests/code       code})))

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

  (create-request relay-id code)

  (ds/gen-key ::m.n.requests/item)

  (q.n.requests/find-by-relay-and-code relay-id code)

  (q.n.requests/delete-all!)

  ds/date

  (ds/->inst)

  nil)
