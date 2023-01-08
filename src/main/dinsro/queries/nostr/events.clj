(ns dinsro.queries.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.events/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.events/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.events/id => (? ::m.n.events/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.events/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.events/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?relay-id]
                :where [[?relay-id ::m.n.events/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.events/addresses "wss://relay.kronkltd.net/"})

  nil)
