(ns dinsro.joins.core.blocks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   [dinsro.specs]))

(defattr index ::m.c.blocks/index :ref
  {ao/target    ::m.c.blocks/id
   ao/pc-output [{::m.c.blocks/index [::m.c.blocks/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.c.blocks/index-ids) :cljs [])]
       {::m.c.blocks/index (m.c.blocks/idents ids)}))})

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
