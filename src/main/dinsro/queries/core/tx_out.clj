(ns dinsro.queries.core.tx-out
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.tx-out/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.tx-out/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-tx
  [tx-id]
  [::m.c.tx/id => (s/coll-of ::m.c.tx-out/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-in-id]
                :in [?tx-id]
                :where [[?tx-in-id ::m.c.tx-out/transaction ?tx-id]]}]
    (map first (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.c.tx-out/id => (? ::m.c.tx-out/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.tx-out/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.tx-out/params => ::m.c.tx-out/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.tx-out/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.tx-out/item)]
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.c.tx-out/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-tx-and-index
  [tx-id n]
  [::m.c.tx-out/transaction ::m.c.tx-out/n => (? ::m.c.tx-out/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-out-id]
                :in    [[?tx-id ?n]]
                :where [[?tx-out-id ::m.c.tx-out/transaction ?tx-id]
                        [?tx-out-id ::m.c.tx-out/n ?n]]}]
    (ffirst (xt/q db query [tx-id n]))))

(>defn update!
  [id params]
  [::m.c.tx-out/id ::m.c.tx-out/params => any?]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old params)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)))

(comment
  (index-records)

  nil)
