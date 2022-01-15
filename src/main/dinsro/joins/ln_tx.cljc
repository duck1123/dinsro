(ns dinsro.joins.ln-tx
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-transactions :as m.ln-tx]
   #?(:clj [dinsro.queries.ln-transactions :as q.ln-tx])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.ln-tx/index :ref
  {ao/target    ::m.ln-tx/id
   ao/pc-output [{::m.ln-tx/index [::m.ln-tx/id]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [ids #?(:clj (q.ln-tx/index-ids) :cljs [])]
       {::m.ln-tx/index (m.ln-tx/idents ids)}))})

(def attributes [index])
