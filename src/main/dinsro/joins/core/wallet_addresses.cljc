(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_)

(defattr index ::index :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::index [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [{ln-node-id ::m.ln.nodes/id} query-params
           ids                          #?(:clj (if ln-node-id
                                                  (q.c.wallet-addresses/find-by-ln-node ln-node-id)
                                                  (q.c.wallet-addresses/index-ids))
                                           :cljs (do
                                                   (comment ln-node-id)
                                                   []))]
       {::index (m.c.wallet-addresses/idents ids)}))})

(defattr index-by-wallet ::index-by-wallet :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::index-by-wallet [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (log/info :index-by-wallet/starting {:query-params query-params})
     (let [ids #?(:clj
                  (if-let [wallet-id (::m.c.wallets/id query-params)]
                    (q.c.wallet-addresses/find-by-wallet wallet-id)
                    (do
                      (log/warn :index-by-wallet/no-wallet-id {:query-params query-params})
                      [])) :cljs [])]
       {::index-by-wallet (m.c.wallet-addresses/idents ids)}))})

(def attributes [index index-by-wallet])
