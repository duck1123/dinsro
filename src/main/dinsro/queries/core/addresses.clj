(ns dinsro.queries.core.addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.addresses/id)]
  (c.xtdb/query-ids '{:find [?e] :where [[?e ::m.c.addresses/id _]]}))

(>defn read-record
  [id]
  [::m.c.addresses/id => (? ::m.c.addresses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.addresses/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.addresses/params => ::m.c.addresses/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.addresses/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.addresses/item)]
  (map read-record (index-ids)))

(defn find-by-ln-node
  [node-id]
  []
  (c.xtdb/query-ids
   '{:find [?address-id]
     :in [[?node-id]]
     :where [[?account-id ::m.ln.accounts/node ?node-id]
             [?account-id ::m.ln.accounts/wallet ?wallet-id]
             [?wallet-address-id ::m.c.wallet-addresses/wallet ?wallet-id]]}

   [node-id]))

(defn find-by-address
  [address]
  (c.xtdb/query-id
   '{:find [?address-id]
     :in [[?address]]
     :where [[?address-id ::m.c.addresses/address ?address]]}
   [address]))
