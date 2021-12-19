(ns dinsro.queries.core-tx-in
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-tx-in/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-tx-in/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-tx
  [tx-id]
  [::m.core-tx/id => (s/coll-of ::m.core-tx-in/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?tx-in-id]
                :in [?tx-id]
                :where [[?tx-in-id ::m.core-tx-in/transaction ?tx-id]]}]
    (map first (xt/q db query tx-id))))

(>defn read-record
  [id]
  [::m.core-tx-in/id => (? ::m.core-tx-in/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-tx-in/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-tx-in/params => ::m.core-tx-in/id]
  (let [node            (c.xtdb/main-node)
        id              (utils/uuid)
        prepared-params (-> params
                            (assoc ::m.core-tx-in/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-tx-in/item)]
  (map read-record (index-ids)))

(comment
  (index-records)

  nil)
