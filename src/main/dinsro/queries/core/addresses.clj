(ns dinsro.queries.core.addresses
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.addresses/id
   :pk      '?address-id
   :clauses [[::m.ln.nodes/id         '?ln-node-id]
             [::m.ln.accounts/id      '?ln-account-id]
             [::m.c.addresses/address '?address]]
   :rules
   (fn [[ln-node-id ln-account-id address] rules]
     (->> rules
          (concat-when ln-node-id
            [['?ln-node-account-id           ::m.ln.accounts/node           '?ln-node-id]
             ['?ln-node-account-id           ::m.ln.accounts/wallet         '?ln-node-wallet-id]
             ['?ln-node-wallet-address-id    ::m.c.wallet-addresses/wallet  '?ln-node-wallet-id]
             ['?ln-node-wallet-address-id    ::m.c.wallet-addresses/address '?address-id]])
          (concat-when ln-account-id
            [['?ln-account-id                ::m.ln.accounts/wallet         '?ln-account-wallet-id]
             ['?ln-account-wallet-address-id ::m.c.wallet-addresses/wallet  '?ln-account-wallet-id]
             ['?ln-account-wallet-address-id ::m.c.wallet-addresses/address '?address-id]])
          (concat-when address
            [['?address-id                   ::m.c.addresses/address        '?address]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [::m.c.addresses/id => (? ::m.c.addresses/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.addresses/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.addresses/params => ::m.c.addresses/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.addresses/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(defn find-by-address
  [address]
  (c.xtdb/query-value
   '{:find [?address-id]
     :in [[?address]]
     :where [[?address-id ::m.c.addresses/address ?address]]}
   [address]))

(defn delete!
  [id]
  (c.xtdb/delete! id))
