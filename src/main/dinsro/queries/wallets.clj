(ns dinsro.queries.wallets
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.specs]
   [taoensso.timbre :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.wallets/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.wallets/name _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.wallets/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.wallets/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.wallets/params => ::m.wallets/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.wallets/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.wallets/item)]
  (map read-record (index-ids)))

(>defn find-ids-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.wallets/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?wallet-id]
                :in    [?user-id]
                :where [[?wallet-id ::m.wallets/user ?user-id]]}]
    (map first (xt/q db query user-id))))

(>defn find-by-core-node
  [node-id]
  [::m.core-nodes/id => (s/coll-of ::m.wallets/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?wallet-id]
                :in    [?node-id]
                :where [[?wallet-id ::m.wallets/node ?node-id]]}]
    (map first (xt/q db query node-id))))
