(ns dinsro.queries.core-peers
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-peers :as m.core-peers]
   [dinsro.specs]
   [taoensso.timbre :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-peers/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-peers/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.core-peers/id => (? ::m.core-peers/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-peers/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-peers/params => ::m.core-peers/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-peers/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-peers/item)]
  (map read-record (index-ids)))

(>defn delete!
  [id]
  [::m.core-peers/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))

(>defn find-by-core-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.core-peers/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?peer-id]
                :in    [?node-id]
                :where [[?peer-id ::m.core-peers/node ?node-id]]}]
    (map first (xt/q db query node-id))))
