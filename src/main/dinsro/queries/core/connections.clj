(ns dinsro.queries.core.connections
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn create-record
  [params]
  [::m.c.connections/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.connections/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.c.connections/id => (? ::m.c.connections/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.connections/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.c.connections/name _]]}))
