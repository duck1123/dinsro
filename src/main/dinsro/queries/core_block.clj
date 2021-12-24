(ns dinsro.queries.core-block
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.specs]
   [taoensso.timbre :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.core-block/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.core-block/id _]]}]
    (map first (xt/q db query))))

(>defn find-by-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.core-block/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?block-id]
                :in    [?node-id]
                :where [[?block-id ::m.core-block/node ?node-id]]}]
    (map first (xt/q db query (log/spy :info node-id)))))

(>defn fetch-by-node-and-height
  [node-id height]
  [::m.core-block/node ::m.core-block/height => (? ::m.core-block/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [?node-id ?height]
                :where [[?id ::m.core-block/node ?node-id]
                        [?id ::m.core-block/height ?height]]}]
    (ffirst (xt/q db query node-id height))))

(>defn read-record
  [id]
  [::m.core-block/id => (? ::m.core-block/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.core-block/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.core-block/params => ::m.core-block/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.core-block/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.core-block/item)]
  (map read-record (index-ids)))

(defn register-block
  [node-id hash height]
  (if-let [block-id (fetch-by-node-and-height node-id height)]
    (do
      (log/info "found")
      block-id)
    (do
      (log/info "not found")
      (let [params {::m.core-block/hash     hash
                    ::m.core-block/height   height
                    ::m.core-block/node     node-id
                    ::m.core-block/fetched? false}]
        (create-record params)))))

(defn update-block
  [id data]
  (let [node   (c.xtdb/main-node)
        db     (c.xtdb/main-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)))

(comment
  2
  :the
  (first (index-records))

  nil)
