(ns dinsro.joins.nostr.filters
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.filters :as m.n.filters]
   #?(:clj [dinsro.queries.nostr.filters :as q.n.filters])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.filters/id
   ao/pc-output [{::index [::m.n.filters/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.filters/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.filters/idents ids)}))})

(def attributes [index])
