(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_)

(defattr index ::m.c.wallet-addresses/index :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::m.c.wallet-addresses/index [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.wallet-addresses/index-ids) :cljs [])]
       {::m.c.wallet-addresses/index (m.c.wallet-addresses/idents ids)}))})

(defattr index-by-wallet ::m.c.wallet-addresses/index-by-wallet :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::m.c.wallet-addresses/index-by-wallet [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (log/info :index-by-wallet/starting {:query-params query-params})
     (let [ids #?(:clj
                  (if-let [wallet-id (::m.c.wallets/id query-params)]
                    (q.c.wallet-addresses/find-by-wallet wallet-id)
                    (do
                      (log/warn :index-by-wallet/no-wallet-id {:query-params query-params})
                      [])) :cljs [])]
       {::m.c.wallet-addresses/index-by-wallet (m.c.wallet-addresses/idents ids)}))})

(def attributes [index index-by-wallet])
