(ns dinsro.queries.nostr.filter-items
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

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

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.filter-items/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.n.filter-items/id _]]}))

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
