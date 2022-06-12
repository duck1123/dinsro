(ns dinsro.joins.core.tx-out
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.tx/_)

(defattr index ::m.c.tx-out/index :ref
  {ao/target    ::m.c.tx-out/id
   ao/pc-output [{::m.c.tx-out/index [::m.c.tx-out/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (log/info :index/starting {})
     (let [ids #?(:clj
                  (let [tx-id (::m.c.tx/id query-params)]
                    (if tx-id
                      (q.c.tx-out/find-by-tx tx-id)
                      (q.c.tx-out/index-ids)))
                  :cljs
                  (do
                    (comment query-params)
                    []))]
       {::m.c.tx-out/index (map (fn [id] {::m.c.tx-out/id id}) ids)}))})

(def attributes [index])
