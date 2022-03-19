(ns dinsro.joins.core.connections
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.connections :as m.c.connections]
   #?(:clj [dinsro.queries.core.connections :as q.c.connections])
   [dinsro.specs]))

(defattr index ::m.c.connections/index :ref
  {ao/cardinality :many
   ao/target      ::m.c.connections/id
   ao/pc-output   [{::m.c.connections/index [::m.c.connections/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.connections/index-ids) :cljs [])]
       {::m.c.connections/index (m.c.connections/idents ids)}))})

(def attributes [index])
