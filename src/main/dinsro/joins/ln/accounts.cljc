(ns dinsro.joins.ln.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   #?(:clj [dinsro.queries.ln.accounts :as q.ln.accounts])
   [dinsro.specs]))

(defattr index ::m.ln.accounts/index :ref
  {ao/target    ::m.ln.accounts/id
   ao/pc-output [{::m.ln.accounts/index [::m.ln.accounts/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.accounts/index-ids) :cljs [])]
       {::m.ln.accounts/index (m.ln.accounts/idents ids)}))})

(def attributes [index])
