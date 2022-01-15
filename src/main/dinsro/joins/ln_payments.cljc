(ns dinsro.joins.ln-payments
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-payments :as m.ln-payments]
   #?(:clj [dinsro.queries.ln-payments :as q.ln-payments])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.ln-payments/index :ref
  {ao/target    ::m.ln-payments/id
   ao/pc-output [{::m.ln-payments/index [::m.ln-payments/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln-payments/index-ids) :cljs [])]
       {::m.ln-payments/index (m.ln-payments/idents ids)}))})

(def attributes [index])
