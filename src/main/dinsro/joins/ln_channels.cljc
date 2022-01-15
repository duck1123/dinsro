(ns dinsro.joins.ln-channels
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln-channels :as m.ln-channels]
   #?(:clj [dinsro.queries.ln-channels :as q.ln-channels])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr index ::m.ln-channels/index :ref
  {ao/target    ::m.ln-channels/id
   ao/pc-output [{::m.ln-channels/index [::m.ln-channels/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln-channels/index-ids) :cljs [])]
       {::m.ln-channels/index (m.ln-channels/idents ids)}))})

(def attributes [index])
