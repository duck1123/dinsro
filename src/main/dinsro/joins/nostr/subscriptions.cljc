(ns dinsro.joins.nostr.subscriptions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::index [::m.n.subscriptions/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.subscriptions/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.subscriptions/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.subscriptions/id
   ao/pc-output [{::admin-index [::m.n.subscriptions/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.subscriptions/index-ids) :cljs [])]
       {::admin-index (m.n.subscriptions/idents ids)}))})

(def attributes [admin-index index])
