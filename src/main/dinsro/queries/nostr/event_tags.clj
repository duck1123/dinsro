(ns dinsro.queries.nostr.event-tags
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/event_tags.clj]]
;; [[../../joins/nostr/event_tags.cljc]]
;; [[../../model/nostr/event_tags.cljc]]
;; [[../../processors/nostr/event_tags.clj]]
;; [[../../queries/nostr/event_tags.clj]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/event_tags_notebook.clj]]

(def model-key ::m.n.event-tags/id)

(def query-info
  {:ident   model-key
   :pk      '?event-tag-id
   :clauses [[::m.n.events/id        '?parent-id]
             [::m.n.events/pubkey    '?pubkey-id]
             [::m.n.event-tags/event '?event-id]]
   :rules
   (fn [[parent-id pubkey-id event-id] rules]
     (->> rules
          (concat-when parent-id
            [['?event-tag-id ::m.n.event-tags/parent '?parent-id]])
          (concat-when pubkey-id
            [['?event-tag-id ::m.n.event-tags/pubkey '?pubkey-id]])
          (concat-when event-id
            [['?event-tag-id ::m.n.event-tags/event  '?event-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.event-tags/params => :xt/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.event-tags/id => (? ::m.n.event-tags/item)]
  (c.xtdb/read model-key id))

(>defn find-by-parent
  [event-id]
  [::m.n.events/id => (s/coll-of ::m.n.event-tags/id)]
  (log/fine :find-by-event/starting {:event-id event-id})
  (c.xtdb/query-values
   '{:find  [?event-tag-id]
     :in    [[?event-id]]
     :where [[?event-tag-id ::m.n.event-tags/parent ?event-id]]}
   [event-id]))

(>defn delete!
  [id]
  [::m.n.events/id => nil?]
  (log/info :delete!/starting {:id id})
  (c.xtdb/delete! id))
