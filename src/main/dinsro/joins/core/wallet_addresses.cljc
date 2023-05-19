(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_)

(def join-info
  (merge
   {:idents m.c.wallet-addresses/idents}
   #?(:clj {:indexer q.c.wallet-addresses/index-ids
            :counter q.c.wallet-addresses/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.wallet-addresses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::index [:total {:results [::m.c.wallet-addresses/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

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

(def attributes [admin-index index index-by-wallet])
