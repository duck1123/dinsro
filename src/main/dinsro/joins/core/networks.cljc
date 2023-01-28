(ns dinsro.joins.core.networks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   #?(:clj [dinsro.queries.core.networks :as q.c.networks])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.c.networks/id
   ao/pc-output [{::index [::m.c.networks/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [{chain-id ::m.c.chains/id} query-params
           ids                        (if chain-id
                                        #?(:clj (q.c.networks/find-by-chain-id chain-id) :cljs [])
                                        #?(:clj (q.c.networks/index-ids) :cljs []))
           objs                       (m.c.networks/idents ids)]
       (log/info :index/starting {:query-params query-params :ids ids})
       {::index objs}))})

(def attributes [index])
