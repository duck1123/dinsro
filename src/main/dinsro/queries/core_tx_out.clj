(ns dinsro.queries.core-tx-out
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   [dinsro.specs]
   [taoensso.timbre :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-tx-out/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-tx-out/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-tx
  [tx-id]
  [::m.core-tx/id => (s/coll-of ::m.core-tx-out/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-in-id]
                :in [?tx-id]
                :where [[?tx-in-id ::m.core-tx-out/transaction ?tx-id]]}]
    (map first (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.core-tx-out/id => (? ::m.core-tx-out/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-tx-out/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-tx-out/params => ::m.core-tx-out/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-tx-out/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-tx-out/item)]
  (map read-record (index-ids)))

(comment
  (index-records)

  nil)
