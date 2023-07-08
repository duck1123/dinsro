(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])))

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

(def attributes [admin-index index])
