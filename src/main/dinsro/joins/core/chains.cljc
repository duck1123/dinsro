(ns dinsro.joins.core.chains
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.chains :as m.c.chains]
   #?(:clj [dinsro.queries.core.chains :as q.c.chains])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.c.chains/id
   ao/pc-output [{::index [::m.c.chains/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.c.chains/index-ids) :cljs [])]
       {::index (m.c.chains/idents ids)}))})

(def attributes [index])
