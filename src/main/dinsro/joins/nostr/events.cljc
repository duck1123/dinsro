(ns dinsro.joins.nostr.events
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.events :as m.n.events]
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::m.n.events/index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::m.n.events/index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::m.n.events/index (m.n.events/idents ids)}))})

(defattr admin-index ::m.n.events/admin-index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::m.n.events/admin-index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       {::m.n.events/admin-index (m.n.events/idents ids)}))})

(def attributes
  [admin-index
   index])
