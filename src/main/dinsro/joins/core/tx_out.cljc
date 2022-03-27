(ns dinsro.joins.core.tx-out
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   #?(:clj [dinsro.queries.core.tx-out :as q.c.tx-out])
   [dinsro.specs]))

(defattr index ::m.c.tx-out/index :ref
  {ao/target    ::m.c.tx-out/id
   ao/pc-output [{::m.c.tx-out/index [::m.c.tx-out/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.tx-out/index-ids) :cljs [])]
       {::m.c.tx-out/index (map (fn [id] {::m.c.tx-out/id id}) ids)}))})

(def attributes [index])
