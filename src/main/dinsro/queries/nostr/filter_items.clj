(ns dinsro.queries.nostr.filter-items
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.requests :as m.n.requests]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/filter_items.clj]]
;; [[../../joins/nostr/filter_items.cljc]]
;; [[../../model/nostr/filter_items.cljc]]
;; [[../../mutations/nostr/filter_items.cljc]]
;; [[../../ui/nostr/pubkeys/items.cljs]]
;; [[../../ui/nostr/filters/filter_items.cljs]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/filter_items_notebook.clj]]

(def model-key ::m.n.filter-items/id)
(def params-key ::m.n.filter-items/params)
(def item-key ::m.n.filter-items/item)

(def query-info
  {:ident   ::m.n.filter-items/id
   :pk      '?badge-awards-id
   :clauses [[::m.n.pubkeys/id  '?pubkey-id]
             [::m.n.events/id   '?event-id]
             [::m.n.requests/id '?request-id]
             [::m.n.filters/id '?filters-id]
             [:kind             '?kind]]
   :rules
   (fn [[pubkey-id event-id request-id filter-id kind] rules]
     (->> rules
          (concat-when pubkey-id
            [['?filter-item-id ::m.n.filter-items/pubkey '?pubkey-id]])
          (concat-when event-id
            [['?filter-item-id ::m.n.filter-items/event  '?event-id]])
          (concat-when request-id
            [['?filter-item-id ::m.n.filter-items/filter '?filter-id]
             ['?filter-id      ::m.n.filters/request     '?request]])
          (concat-when kind
            [['?filter-item-id ::m.n.filter-items/kind   '?kind]])
          (concat-when filter-id
            [['?filter-item-id ::m.n.filter-items/filter '?filter-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.filter-items/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.filter-items/id => (? ::m.n.filter-items/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.filter-items/id => nil?]
  (c.xtdb/delete! id))

(>defn find-by-filter
  [filter-id]
  [::m.n.filters/id => (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-values
   '{:find  [?item-id]
     :in    [[?filter-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]]}
   [filter-id]))
