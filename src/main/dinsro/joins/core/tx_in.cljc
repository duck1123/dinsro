(ns dinsro.joins.core.tx-in
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   #?(:clj [dinsro.queries.core.tx-in :as q.c.tx-in])
   [dinsro.specs]))

(defattr index ::m.c.tx-in/index :ref
  {ao/target    ::m.c.tx-in/id
   ao/pc-output [{::m.c.tx-in/index [::m.c.tx-in/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.tx-in/index-ids) :cljs [])]
       {::m.c.tx-in/index (map (fn [id] {::m.c.tx-in/id id}) ids)}))})

(def attributes [index])
