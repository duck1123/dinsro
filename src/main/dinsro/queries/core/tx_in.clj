(ns dinsro.queries.core.tx-in
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.tx-in/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.tx-in/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-tx
  [tx-id]
  [::m.c.transactions/id => (s/coll-of ::m.c.tx-in/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-in-id]
                :in    [?tx-id]
                :where [[?tx-in-id ::m.c.tx-in/transaction ?tx-id]]}]
    (map first (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.c.tx-in/id => (? ::m.c.tx-in/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.tx-in/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.tx-in/params => ::m.c.tx-in/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.tx-in/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.tx-in/item)]
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.c.tx-in/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(comment
  (index-records)

  nil)
