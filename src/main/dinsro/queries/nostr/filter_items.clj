(ns dinsro.queries.nostr.filter-items
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.requests :as m.n.requests]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [../../actions/nostr/filter_items.clj]
;; [../../model/nostr/filter_items.cljc]
;; [../../mutations/nostr/filter_items.cljc]
;; [../../joins/nostr/filter_items.cljc]
;; [../../ui/nostr/pubkeys/items.cljs]
;; [../../ui/nostr/filters/filter_items.cljs]

(def query-info
  {:ident   ::m.n.filter-items/id
   :pk      '?badge-awards-id
   :clauses [[::m.n.pubkeys/id  '?pubkey-id]
             [::m.n.events/id   '?event-id]
             [::m.n.requests/id '?request-id]
             [:kind             '?kind]]
   :rules
   (fn [[pubkey-id event-id request-id kind] rules]
     (->> rules
          (concat-when pubkey-id
            [['?filter-item-id ::m.n.filter-items/pubkey '?pubkey-id]])
          (concat-when event-id
            [['?filter-item-id ::m.n.filter-items/event  '?event-id]])
          (concat-when request-id
            [['?filter-item-id ::m.n.filter-items/filter '?filter-id]
             ['?filter-id      ::m.n.filters/request     '?request]])
          (concat-when kind
            [['?filter-item-id ::m.n.filter-items/kind   '?kind]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(def ident-key ::m.n.filter-items/id)
(def params-key ::m.n.filter-items/params)
(def item-key ::m.n.filter-items/item)

(>defn create-record
  [params]
  [::m.n.filter-items/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/get-node)
        params (assoc params ident-key id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.n.filter-items/id => (? ::m.n.filter-items/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ident-key)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.filter-items/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-filter
  [filter-id]
  [::m.n.filters/id => (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-values
   '{:find  [?item-id]
     :in    [[?filter-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]]}
   [filter-id]))

(>defn find-by-request
  [request-id]
  [::m.n.requests/id => (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-values
   '{:find  [?item-id]
     :in    [[?request-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]
             [?filter-id ::m.n.filters/request ?request-id]]}
   [request-id]))
