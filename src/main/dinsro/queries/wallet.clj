(ns dinsro.queries.wallet
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.model.wallet :as m.wallet]
   [dinsro.specs]
   [taoensso.timbre :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.wallet/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.wallet/name _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.wallet/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.wallet/name)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.wallet/params => ::m.wallet/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.wallet/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.wallet/item)]
  (map read-record (index-ids)))

(>defn find-ids-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.wallet/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?wallet-id]
                :in    [?user-id]
                :where [[?wallet-id ::m.wallet/user ?user-id]]}]
    (map first (xt/q db query user-id))))

(>defn find-by-core-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.wallet/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?wallet-id]
                :in    [?node-id]
                :where [[?wallet-id ::m.wallet/node ?node-id]]}]
    (map first (xt/q db query node-id))))
