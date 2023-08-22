(ns dinsro.queries.core.wallet-addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../mutations/core/wallet_addresses.cljc]]

(def query-info
  {:ident        ::m.c.wallet-addresses/id
   :pk           '?wallet-address-id
   :clauses      [[:actor/id          '?actor-id]
                  [:actor/admin?      '?admin?]
                  [::m.c.addresses/id '?address-id]
                  [::m.c.networks/id  '?network-id]
                  [::m.c.wallets/id   '?wallet-id]]
   :sort-columns {::m.c.wallet-addresses/path-index '?path-index}
   :rules
   (fn [[_actor-id admin? address-id
         network-id wallet-id] rules]
     (->> rules
          (concat-when (not admin?)
            [['?wallet-address-id ::m.c.wallet-addresses/wallet  '?auth-wallet-id]
             ['?auth-wallet-id    ::m.c.wallets/user             '?actor-id]])
          (concat-when address-id
            [['?wallet-address-id ::m.c.wallet-addresses/address '?address-id]])
          (concat-when wallet-id
            [['?wallet-address-id ::m.c.wallet-addresses/wallet  '?wallet-id]])
          (concat-when network-id
            [['?wallet-address-id ::m.c.wallet-addresses/wallet  '?network-wallet-id]
             ['?network-wallet-id ::m.c.wallets/network          '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.c.wallet-addresses/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.wallet-addresses/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.wallet-addresses/params => ::m.c.wallet-addresses/id]
  (let [node            (c.xtdb/get-node)
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
  (let [ids (c.xtdb/query-values
             '{:find  [?wallet-address-id]
               :in    [[?ln-node-id]]
               :where [[?account-id ::m.ln.accounts/node ?ln-node-id]
                       [?account-id ::m.ln.accounts/wallet ?wallet-id]
                       [?wallet-address-id ::m.c.wallet-addresses/wallet ?wallet-id]]}
             [ln-node-id])]
    (log/trace :find-by-ln-node/finished {:ids ids})
    ids))

(>defn find-by-wallet
  [wallet-id]
  [::m.c.wallets/id => (s/coll-of ::m.c.wallet-addresses/id)]
  (c.xtdb/query-values
   '{:find  [?address-id]
     :in    [?wallet-id]
     :where [[?address-id ::m.c.wallet-addresses/wallet ?wallet-id]]}
   [wallet-id]))

(>defn find-by-wallet-and-index
  [wallet-id index]
  [::m.c.wallets/id ::m.c.wallet-addresses/path-index => (? ::m.c.wallet-addresses/id)]
  (c.xtdb/query-value
   '{:find  [?address-id]
     :in    [[?wallet-id ?index]]
     :where [[?address-id ::m.c.wallet-addresses/wallet ?wallet-id]
             [?address-id ::m.c.wallet-addresses/path-index ?index]]}
   [wallet-id index]))
