(ns dinsro.joins.core.tx-out
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.transactions/_)

(defattr index ::index :ref
  {ao/target    ::m.c.tx-out/id
   ao/pc-output [{::index [::m.c.tx-out/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (log/info :index/starting {})
     (let [ids #?(:clj  (if-let [tx-id (::m.c.transactions/id query-params)]
                          (q.c.tx-out/find-by-tx tx-id)
                          (q.c.tx-out/index-ids))
                  :cljs (do (comment query-params) []))]
       {::index (m.c.tx-out/idents ids)}))})

(def attributes [index])
