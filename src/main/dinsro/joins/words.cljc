(ns dinsro.joins.words
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.words :as m.words]
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   #?(:clj [dinsro.queries.words :as q.words])
   #?(:clj [dinsro.queries.wallet-addresses :as q.wallet-addresses])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.words/index :ref
  {ao/target    ::m.words/id
   ao/pc-output [{::m.words/index [::m.words/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.words/index-ids) :cljs [])]
       {::m.words/index (m.words/idents ids)}))})

(def attributes [index])
