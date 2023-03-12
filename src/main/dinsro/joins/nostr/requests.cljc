(ns dinsro.joins.nostr.requests
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.requests/id
   ao/pc-output [{::index [::m.n.requests/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.requests/index-ids)
                  :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.requests/idents ids)}))})

(def attributes [index])
