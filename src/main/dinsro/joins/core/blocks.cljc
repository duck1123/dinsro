(ns dinsro.joins.core.blocks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.nodes/_ ::log/_)

#?(:clj
   (defn do-index
     [{:keys [query-params]} props]
     (log/info :do-index/starting
               {:props        props
                :query-params query-params})
     (let [{node-id ::m.c.nodes/id} query-params]
       (log/info :do-index/params-parsed {:node-id node-id})
       (let [ids    (if node-id
                      (q.c.blocks/find-by-node node-id)
                      (q.c.blocks/index-ids))
             idents (m.c.blocks/idents ids)]
         (log/info :do-index/results {:idents idents})
         {::m.c.blocks/index idents}))))

(defattr index ::m.c.blocks/index :ref
  {ao/target    ::m.c.blocks/id
   ao/pc-output [{::m.c.blocks/index [::m.c.blocks/id]}]
   ao/pc-resolve
   (fn [env props]
     #?(:clj  (do-index env props)
        :cljs (let [_ [env props]] {::m.c.blocks/index []})))})

(defattr transactions ::m.c.blocks/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.blocks/id}
   ao/pc-output   [{::m.c.blocks/transactions [::m.c.tx/id]}]
   ao/target      ::m.c.tx/id
   ao/pc-resolve
   (fn [_env {::m.c.blocks/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx/find-by-block id) :cljs []) [])]
       {::m.c.blocks/transactions (m.c.tx/idents ids)}))})

(def attributes
  [index
   transactions])
