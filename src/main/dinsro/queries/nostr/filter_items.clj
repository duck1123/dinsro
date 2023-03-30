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

(def ident-key ::m.n.filters/id)
(def params-key ::m.n.filters/params)
(def item-key ::m.n.filters/item)

(>defn create-record
  [params]
  [::m.n.filters/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ident-key id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/finer :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.n.filters/id => (? ::m.n.filters/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ident-key)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.filters/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.n.filters/id _]]}))

(>defn delete!
  [id]
  [::m.n.filters/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(defn find-by-filter
  [filter-id]
  (c.xtdb/query-ids
   '{:find [?item-id]
     :in [[?filter-id]]
     :where [[?item-id ::m.n.filter-items/filter ?filter-id]]}
   [filter-id]))