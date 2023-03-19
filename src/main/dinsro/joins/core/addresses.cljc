(ns dinsro.joins.core.addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   #?(:clj [dinsro.queries.core.addresses :as q.c.addresses])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.c.addresses/id
   ao/pc-output [{::index [::m.c.addresses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _props]
     (let [{node-id ::m.ln.nodes/id} query-params
           ids                       #?(:clj (if node-id
                                               (q.c.addresses/find-by-ln-node node-id)
                                               (q.c.addresses/index-ids))
                                        :cljs (do (comment node-id) []))]
       {::index (m.c.addresses/idents ids)}))})

(def attributes [index])
