(ns dinsro.joins.wallets
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   #?(:clj [dinsro.queries.wallets :as q.wallets])
   #?(:clj [dinsro.queries.wallet-addresses :as q.wallet-addresses])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.wallets/index :ref
  {ao/target    ::m.wallets/id
   ao/pc-output [{::m.wallets/index [::m.wallets/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.wallets/index-ids) :cljs [])]
       {::m.wallets/index (m.wallets/idents ids)}))})

(defattr addresses ::m.wallets/addresses :ref
  {ao/target           ::m.wallet-addresses/id
   ao/pc-input         #{::m.wallets/id}
   ao/pc-output        [{::m.wallets/addresses [::m.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env {::m.wallets/keys [id] :as props}]
     (log/spy :info props)
     (let [ids (if id #?(:clj (q.wallet-addresses/find-by-wallet (log/spy :info id)) :cljs []) [])]
       {::m.wallets/addresses (m.wallet-addresses/idents (log/spy :info ids))}))
   ::report/column-EQL {::m.wallets/addresses [::m.wallet-addresses/id ::m.wallet-addresses/address]}})

(def attributes [index addresses])
