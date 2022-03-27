(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   [dinsro.specs]))

(defattr index ::m.c.wallet-addresses/index :ref
  {ao/target    ::m.c.wallet-addresses/id
   ao/pc-output [{::m.c.wallet-addresses/index [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.wallet-addresses/index-ids) :cljs [])]
       {::m.c.wallet-addresses/index (m.c.wallet-addresses/idents ids)}))})

(def attributes [index])
