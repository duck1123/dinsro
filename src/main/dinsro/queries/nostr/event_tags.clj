(ns dinsro.queries.nostr.event-tags
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/event_tags.clj][Event Tag Actions]]
;; [[../../model/nostr/event_tags.cljc][Event Tags Model]]

(>defn create-record
  [params]
  [::m.n.event-tags/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.event-tags/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.event-tags/id => (? ::m.n.event-tags/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.event-tags/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.event-tags/id)]
  (log/info :index-ids/starting {})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?relay-id]
                :where [[?relay-id ::m.n.event-tags/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

(>defn find-by-event
  [event-id]
  [::m.n.events/id => (s/coll-of ::m.n.event-tags/id)]
  (log/fine :find-by-event/starting {:event-id event-id})
  (let [db     (c.xtdb/main-db)
        query  '{:find  [?event-tag-id]
                 :in    [[?event-id]]
                 :where [[?event-tag-id ::m.n.event-tags/event ?event-id]]}
        result (xt/q db query [event-id])
        ids    (map first result)]
    (log/finer :find-by-event/finished {:ids ids})
    ids))

(>defn delete!
  [id]
  [::m.n.events/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

;; (defn delete!
;;   [event-id]
;;   (log/info :delete!/starting {:event-id event-id})

;;   )
