(ns dinsro.queries.nostr.filter-items
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
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

(def ident-key ::m.n.filter-items/id)
(def params-key ::m.n.filter-items/params)
(def item-key ::m.n.filter-items/item)

(>defn create-record
  [params]
  [::m.n.filter-items/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ident-key id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.n.filter-items/id => (? ::m.n.filter-items/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ident-key)
      (dissoc record :xt/id))))

(defn get-index-query
  [query-params]
  (let [pubkey-id  (::m.n.pubkeys/id query-params)
        event-id   (::m.n.events/id query-params)
        request-id (::m.n.requests/id query-params)
        kind       (:kind query-params)]
    {:find  ['?filter-item-id]
     :in    [['?pubkey-id '?event-id '?request-id '?kind]]
     :where (->> [['?filter-item-id ::m.n.filter-items/id '_]]
                 (concat (when pubkey-id
                           [['?filter-item-id ::m.n.filter-items/pubkey '?pubkey-id]]))
                 (concat (when event-id
                           [['?filter-item-id ::m.n.filter-items/event '?event-id]]))
                 (concat (when request-id
                           [['?filter-item-id ::m.n.filter-items/filter '?filter-id]
                            ['?filter-id ::m.n.filters/request '?request]]))
                 (concat (when kind
                           [['?filter-item-id ::m.n.filter-items/kind '?kind]]))
                 (filter identity)
                 (into []))}))

(defn count-ids
  ([]
   (count-ids {}))
  ([query-params]
   (log/info :count-ids/starting {:query-params query-params})
   (let [base-params  (get-index-query query-params)
         limit-params {:find ['(count ?filter-item-id)]}
         query        (merge base-params limit-params)
         params       []]
     (log/info :count-ids/query {:query query :params params})
     (let [c (c.xtdb/query-id query params)]
       (or c 0)))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.filter-items/id)]
   (index-ids {}))
  ([query-params]
   [map? => (s/coll-of ::m.n.filter-items/id)]
   (do
     (log/info :index-ids/starting {:query-params query-params})
     (let [{:indexed-access/keys [options]
            pubkey-id            ::m.n.pubkeys/id
            event-id             ::m.n.events/id
            request-id           ::m.n.requests/id
            kind                 ::m.n.filter-items/kind} query-params
           {:keys [limit offset] :or {limit 20 offset 0}} options
           base-params                                    (get-index-query query-params)
           limit-params                                   {:limit limit :offset offset}
           query                                          (merge base-params limit-params)
           params                                         [pubkey-id event-id request-id kind]]
       (log/info :index-ids/running {:query query :params params})
       (let [ids (c.xtdb/query-ids query params)]
         (log/trace :index-ids/finished {:ids ids})
         ids)))))

(>defn delete!
  [id]
  [::m.n.filter-items/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn find-by-filter
  [filter-id]
  [::m.n.filters/id => (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-ids
   '{:find  [?item-id]
     :in    [[?filter-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]]}
   [filter-id]))

(>defn find-by-request
  [request-id]
  [::m.n.requests/id => (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-ids
   '{:find  [?item-id]
     :in    [[?request-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]
             [?filter-id ::m.n.filters/request ?request-id]]}
   [request-id]))
