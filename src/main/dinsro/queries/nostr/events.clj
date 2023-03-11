(ns dinsro.queries.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/events.clj][Event Actions]]
;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../joins/nostr/events.cljc][Event Joins]]

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
  (c.xtdb/query-ids '{:find [?relay-id] :where [[?relay-id ::m.n.events/id _]]}))

(>defn find-by-author
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.n.events/id)]
  (log/fine :find-by-author/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-ids
   '{:find  [?event-id]
     :in    [[?pubkey-id]]
     :where [[?event-id ::m.n.events/pubkey ?pubkey-id]]}
   [pubkey-id]))

(>defn find-by-note-id
  [note-id]
  [::m.n.events/note-id => (? ::m.n.events/id)]
  (log/finer :find-by-note-id/starting {:note-id note-id})
  (c.xtdb/query-id
   '{:find  [?event-id]
     :in    [[?note-id]]
     :where [[?event-id ::m.n.events/note-id ?note-id]]}
   [note-id]))

(>defn register-event!
  [params]
  [::m.n.events/params => ::m.n.events/id]
  (let [note-id (::m.n.events/note-id params)]
    (if-let [event-id (find-by-note-id note-id)]
      event-id
      (do
        (log/info :register-event!/creating {:params params})
        (create-record params)))))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.events/addresses "wss://relay.kronkltd.net/"})

  nil)
