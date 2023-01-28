(ns dinsro.joins.core.words
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_)

(defattr index ::index :ref
  {ao/target    ::m.c.words/id
   ao/pc-output [{::index [::m.c.words/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (log/info :index/starting {:query-params query-params})
     (let [ids #?(:clj
                  (if-let [wallet-id (::m.c.wallets/id query-params)]
                    (q.c.words/find-by-wallet wallet-id)
                    (q.c.words/index-ids))
                  :cljs [])]
       {::index (m.c.words/idents ids)}))})

(def attributes [index])
