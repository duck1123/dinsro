(ns dinsro.queries.ln.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.ln.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.ln.nodes/name _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.nodes/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.nodes/name)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.nodes/params => ::m.ln.nodes/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.nodes/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.ln.nodes/item)]
  (map read-record (index-ids)))

(defn create-transaction
  [params]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc :m.ln.tx/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.ln.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?user-id]
                :where [[?node-id ::m.ln.nodes/user ?user-id]]}]
    (map first (xt/q db query user-id))))

(>defn find-id-by-user-and-name
  [user-id name]
  [::m.users/id ::m.ln.nodes/name => (? ::m.ln.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?user-id ?name]
                :where [[?node-id ::m.ln.nodes/user ?user-id]
                        [?node-id ::m.ln.nodes/name ?name]]}]
    (ffirst (xt/q db query user-id name))))

(>defn find-by-core-node
  [core-node-id]
  [::m.c.nodes/id => (s/coll-of ::m.ln.nodes/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?node-id]
                :in    [?core-node-id]
                :where [[?node-id ::m.ln.nodes/core-node ?core-node-id]]}]
    (map first (xt/q db query core-node-id))))
