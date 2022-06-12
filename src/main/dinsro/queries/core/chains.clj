(ns dinsro.queries.core.chains
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.c.chains/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.chains/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [::m.c.chains/id => (? ::m.c.chains/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.chains/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.chains/id _]]}]
    (map first (xt/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.chains/item)]
  (map read-record (index-ids)))

(>defn find-id-by-name
  [name]
  [::m.c.chains/name => (? ::m.c.chains/id)]
  (log/info :find-id-by-name/starting {:name name})
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?name]
                :where [[?node-id ::m.c.chains/name ?name]]}
        result (ffirst (xt/q db query name))]
    (log/info :find-id-by-name/finished {:result result})
    result))

(>defn delete!
  [id]
  [::m.c.chains/id => any?]
  (let [node (c.xtdb/main-node)
        tx   (xt/submit-tx node [[::xt/evict id]])]
    (xt/await-tx node tx)))
