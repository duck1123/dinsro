(ns dinsro.queries.core.wallet-addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.wallet-addresses/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.wallet-addresses/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [:xt/id => (? ::m.c.wallet-addresses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.wallet-addresses/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.wallet-addresses/params => ::m.c.wallet-addresses/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.wallet-addresses/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.wallet-addresses/item)]
  (map read-record (index-ids)))

(defn find-by-ln-node
  [ln-node-id]
  (log/debug :find-by-ln-node/starting {:ln-node-id ln-node-id})
  (let [ids (c.xtdb/query-ids
             '{:find  [?wallet-address-id]
               :in    [[?ln-node-id]]
               :where [[?account-id ::m.ln.accounts/node ?ln-node-id]
                       [?account-id ::m.ln.accounts/wallet ?wallet-id]
                       [?wallet-address-id ::m.c.wallet-addresses/wallet ?wallet-id]]}
             [ln-node-id])]
    (log/finer :find-by-ln-node/finished {:ids ids})
    ids))

(>defn find-by-wallet
  [wallet-id]
  [::m.c.wallets/id => (s/coll-of ::m.c.wallet-addresses/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?address-id]
                :in    [?wallet-id]
                :where [[?address-id ::m.c.wallet-addresses/wallet ?wallet-id]]}]
    (map first (xt/q db query wallet-id))))

(>defn find-by-wallet-and-index
  [wallet-id index]
  [::m.c.wallets/id ::m.c.wallet-addresses/path-index => (? ::m.c.wallet-addresses/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?address-id]
                :in    [[?wallet-id ?index]]
                :where [[?address-id ::m.c.wallet-addresses/wallet ?wallet-id]
                        [?address-id ::m.c.wallet-addresses/path-index ?index]]}]
    (ffirst (xt/q db query [wallet-id index]))))
