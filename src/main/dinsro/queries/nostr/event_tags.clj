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
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.event-tags/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.event-tags/id => (? ::m.n.event-tags/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.event-tags/id)
      (dissoc record :xt/id))))

(defn get-index-query
  [_query-params]
  {:find ['?event-tag-id]
   :where [['?event-tag-id ::m.n.event-tags/id '_]]})

(defn count-ids
  []
  (or (c.xtdb/query-value
       '{:find  [(count ?relay-id)]
         :where [[?relay-id ::m.n.event-tags/id _]]})
      0))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.event-tags/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.n.event-tags/id)]
   (do
     (log/info :index-ids/starting {})
     (let [{:indexed-access/keys [options]} query-params
           {:keys [limit offset]
            :or   {limit 20 offset 0}}      options
           base-params                      (get-index-query query-params)
           limit-params                     {:limit limit :offset offset}
           query                            (merge base-params limit-params)]
       (log/debug :index-ids/running {:query query})
       (let [ids (c.xtdb/query-values query)]
         (log/trace :index-ids/finished {:ids ids})
         ids)))))

(>defn find-by-parent
  [event-id]
  [::m.n.events/id => (s/coll-of ::m.n.event-tags/id)]
  (log/fine :find-by-event/starting {:event-id event-id})
  (c.xtdb/query-values
   '{:find  [?event-tag-id]
     :in    [[?event-id]]
     :where [[?event-tag-id ::m.n.event-tags/parent ?event-id]]}
   [event-id]))

(>defn find-by-event
  [event-id]
  [::m.n.events/id => (s/coll-of ::m.n.event-tags/id)]
  (log/fine :find-by-event/starting {:event-id event-id})
  (c.xtdb/query-values
   '{:find  [?event-tag-id]
     :in    [[?event-id]]
     :where [[?event-tag-id ::m.n.event-tags/event ?event-id]]}
   [event-id]))

(>defn delete!
  [id]
  [::m.n.events/id => nil?]
  (log/info :delete!/starting {:id id})
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))
