(ns dinsro.actions.nostr.filters
  (:require
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [lambdaisland.glogc :as log]))

(defn add-filter!
  [request-id]
  (log/info :add-filter!/starting {:request-id request-id})
  (let [n (q.n.filters/get-greatest-index request-id)]
    (q.n.filters/create-record
     {::m.n.filters/index   (inc n)
      ::m.n.filters/request request-id})))

(defn register-filter!
  [request-id]
  (add-filter! request-id))

(defn do-add-filters!
  [props]
  (log/info :do-add-filters!/starting {:props props})
  (if-let [request-id (::m.n.requests/id props)]
    (do
      (add-filter! request-id)
      {::mu/status :ok})
    {::mu/status :fail
     ::mu/errors ["No request id"]}))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props})
  {::mu/status :ok})

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (def request-id (first (q.n.requests/index-ids)))
  request-id

  (q.n.filters/get-greatest-index request-id)

  (add-filter! request-id)

  (q.n.filters/index-ids)

  (do-add-filters! {::m.n.requests/id request-id})

  (map q.n.filters/read-record (q.n.filters/index-ids))

  nil)
