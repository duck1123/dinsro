(ns dinsro.queries.core.tx-out
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.tx-out/id)]
  (c.xtdb/query-values '{:find [?e] :where [[?e ::m.c.tx-out/id _]]}))

(>defn find-by-tx
  [tx-id]
  [::m.c.transactions/id => (s/coll-of ::m.c.tx-out/id)]
  (c.xtdb/query-values
   '{:find  [?tx-in-id]
     :in    [[?tx-id]]
     :where [[?tx-in-id ::m.c.tx-out/transaction ?tx-id]]}
   [tx-id]))

(>defn read-record
  [id]
  [::m.c.tx-out/id => (? ::m.c.tx-out/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.tx-out/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.tx-out/params => ::m.c.tx-out/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.tx-out/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn delete!
  [id]
  [::m.c.tx-out/id => any?]
  (let [node (c.xtdb/get-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-tx-and-index
  [tx-id n]
  [::m.c.tx-out/transaction ::m.c.tx-out/n => (? ::m.c.tx-out/id)]
  (c.xtdb/query-value
   '{:find  [?tx-out-id]
     :in    [[?tx-id ?n]]
     :where [[?tx-out-id ::m.c.tx-out/transaction ?tx-id]
             [?tx-out-id ::m.c.tx-out/n ?n]]}
   [tx-id n]))

(>defn find-by-tx-id-and-index
  [tx-id n]
  [::m.c.transactions/tx-id ::m.c.tx-out/n => (? ::m.c.tx-out/id)]
  (c.xtdb/query-value
   '{:find  [?tx-out-id]
     :in    [[?tx-id ?n]]
     :where [[?transaction-id ::m.c.transactions/tx-id           ?tx-id]
             [?tx-out-id      ::m.c.tx-out/transaction ?transaction-id]
             [?tx-out-id      ::m.c.tx-out/n           ?n]]}
   [tx-id n]))

(>defn update!
  [id params]
  [::m.c.tx-out/id ::m.c.tx-out/params => any?]
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        old    (xt/pull db '[*] id)
        params (merge old params)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)))
