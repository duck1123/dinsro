(ns dinsro.joins.core.peers
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   #?(:clj [dinsro.queries.core.peers :as q.c.peers])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::m.c.peers/index :ref
  {ao/target    ::m.c.peers/id
   ao/pc-output [{::m.c.peers/index [::m.c.peers/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :peers/indexing {:query-params query-params :props props})
     (let [{node-id ::m.c.nodes/id} query-params
           ids                      (if node-id
                                      #?(:clj  (q.c.peers/find-by-core-node node-id)
                                         :cljs [])
                                      #?(:clj  (q.c.peers/index-ids)
                                         :cljs []))]
       {::m.c.peers/index (map (fn [id] {::m.c.peers/id id}) ids)}))})

(def attributes [index])
