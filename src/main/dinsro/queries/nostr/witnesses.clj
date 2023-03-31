(ns dinsro.queries.nostr.witnesses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   ;; [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.events :as m.n.events]
   ;; [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.n.witnesses/params => ::m.n.witnesses/id]
  (log/debug :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (merge
                         {::m.n.witnesses/id id
                          :xt/id             id}
                         params)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.witnesses/id)]
  (log/debug :index-ids/starting {})
  (let [ids (c.xtdb/query-ids '{:find [?id] :where [[?id ::m.n.witnesses/id _]]})]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn read-record
  [id]
  [::m.n.witnesses/id => (? ::m.n.witnesses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (log/debug :read-record/starting {:record record})
    (when (get record ::m.n.witnesses/id)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.witnesses/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-event-and-run
  [event-id run-id]
  [::m.n.events/id  ::m.n.runs/id => (? ::m.n.witnesses/id)]
  (log/trace :find-by-event-and-run/starting {:event-id event-id :run-id run-id})
  (c.xtdb/query-id
   '{:find  [?witness-id]
     :in    [[?event-id ?run-id]]
     :where [[?witness-id ::m.n.witnesses/event ?event-id]
             [?witness-id ::m.n.witnesses/run ?run-id]]}
   [event-id run-id]))
