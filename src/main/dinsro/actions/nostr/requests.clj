(ns dinsro.actions.nostr.requests
  (:require
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(defn create-request
  [relay-id code]
  (if-let [request-id (q.n.requests/find-by-relay-and-code relay-id code)]
    (throw (ex-info (str "request already exists - " request-id) {}))
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
  (if-let [relay-id (q.n.requests/find-relay request-id)]
    (if-let [filters (q.n.filters/find-by-request request-id)]
      (do
        (log/info :start!/filters {:filters filters :relay-id relay-id})
        (doseq [filter-id filters]
          (let [items (q.n.filter-items/find-by-filter filter-id)]
            (log/info :start!/items {:items items}))))
      (throw (ex-info "No Filters" {})))
    (throw (ex-info "No Relay" {}))))

(defn stop!
  [request-id]
  (log/info :start!/starting {:request-id request-id})
  (if-let [relay-id (q.n.requests/find-relay request-id)]
    relay-id
    (throw (ex-info "No Relay" {}))))

(defn do-start!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (start! request-id)
      {::mu/status :ok})
    (throw (ex-info "No request id" {}))))

(defn do-stop!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (log/info :do-stop!/starting {:request-id request-id})
      (try
        (stop! request-id)
        {:status "ok"}
        (catch Exception ex (mu/exception-response ex))))
    (mu/error-response "No Request id")))

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
