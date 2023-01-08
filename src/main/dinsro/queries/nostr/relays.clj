(ns dinsro.queries.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.relays/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.relays/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.relays/id => (? ::m.n.relays/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.relays/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.relays/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?relay-id]
                :where [[?relay-id ::m.n.relays/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.relays/addresses "wss://relay.kronkltd.net/"})

  nil)
