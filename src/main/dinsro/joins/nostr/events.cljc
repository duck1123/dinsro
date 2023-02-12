(ns dinsro.joins.nostr.events
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.events :as m.n.events]
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.events/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::admin-index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       {::admin-index (m.n.events/idents ids)}))})

(def attributes [admin-index index])
