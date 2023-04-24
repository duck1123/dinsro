(ns dinsro.joins.nostr.event-tags
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   #?(:clj [dinsro.queries.nostr.event-tags :as q.n.event-tags])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/event_tags.cljc][Event Tags Model]]

(defattr index ::index :ref
  {ao/target    ::m.n.event-tags/id
   ao/pc-output [{::index [::m.n.event-tags/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.event-tags/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       (let [total #?(:clj (q.n.event-tags/count-ids) :cljs 0)
             results (m.n.event-tags/idents ids)]
         {::index {:total total :results results}})))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.event-tags/id
   ao/pc-output [{::admin-index [::m.n.event-tags/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.event-tags/index-ids) :cljs [])]
       {::admin-index (m.n.event-tags/idents ids)}))})

(def attributes [admin-index index])
