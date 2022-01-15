(ns dinsro.joins.ln-payreqs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-payreqs :as m.ln-payreqs]
   #?(:clj [dinsro.queries.ln-payreqs :as q.ln-payreqs])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.ln-payreqs/index :ref
  {ao/target    ::m.ln-payreqs/id
   ao/pc-output [{::m.ln-payreqs/index [::m.ln-payreqs/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln-payreqs/index-ids) :cljs [])]
       {::m.ln-payreqs/index (m.ln-payreqs/idents ids)}))})

(def attributes [index])
