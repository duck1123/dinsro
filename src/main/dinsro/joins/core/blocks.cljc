(ns dinsro.joins.core.blocks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.tx :as m.core-tx]
   #?(:clj [dinsro.queries.core.blocks :as q.core-blocks])
   #?(:clj [dinsro.queries.core.tx :as q.core-tx])
   [dinsro.specs]))

(defattr index ::m.core-blocks/index :ref
  {ao/target    ::m.core-blocks/id
   ao/pc-output [{::m.core-blocks/index [::m.core-blocks/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.core-blocks/index-ids) :cljs [])]
       {::m.core-blocks/index (m.core-blocks/idents ids)}))})

(defattr transactions ::m.core-blocks/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-blocks/id}
   ao/pc-output   [{::m.core-blocks/transactions [::m.core-tx/id]}]
   ao/target      ::m.core-tx/id
   ao/pc-resolve
   (fn [_env {::m.core-blocks/keys [id]}]
     (let [ids (if id #?(:clj (q.core-tx/find-by-block id) :cljs []) [])]
       {::m.core-blocks/transactions (m.core-tx/idents ids)}))})

(def attributes
  [index
   transactions])
