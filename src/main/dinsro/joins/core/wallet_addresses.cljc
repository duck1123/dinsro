(ns dinsro.joins.core.wallet-addresses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.wallet-addresses :as m.wallet-addresses]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.wallet-addresses])
   [dinsro.specs]))

(defattr index ::m.wallet-addresses/index :ref
  {ao/target    ::m.wallet-addresses/id
   ao/pc-output [{::m.wallet-addresses/index [::m.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.wallet-addresses/index-ids) :cljs [])]
       {::m.wallet-addresses/index (m.wallet-addresses/idents ids)}))})

(def attributes [index])
