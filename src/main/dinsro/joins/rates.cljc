(ns dinsro.joins.rates
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])))

(defattr index ::m.rates/index :ref
  {ao/target    ::m.rates/id
   ao/pc-output [{::m.rates/index [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.rates/index-ids) :cljs [])]
       {::m.rates/index (m.rates/idents ids)}))})

(def attributes [index])
