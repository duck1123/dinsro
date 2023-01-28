(ns dinsro.joins.ln.invoices
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   #?(:clj [dinsro.queries.ln.invoices :as q.ln.invoices])
   [dinsro.specs]))

(defattr index ::index :ref
  {ao/target    ::m.ln.invoices/id
   ao/pc-output [{::index [::m.ln.invoices/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.invoices/index-ids) :cljs [])]
       {::index (m.ln.invoices/idents ids)}))})

(def attributes [index])
