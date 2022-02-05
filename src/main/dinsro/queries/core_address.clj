(ns dinsro.queries.core-address
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-address :as m.core-address]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-address/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-address/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.core-address/id => (? ::m.core-address/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-address/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-address/params => ::m.core-address/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-address/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-address/item)]
  (map read-record (index-ids)))

(comment
  2
  :the
  (first (index-records))

  nil)
