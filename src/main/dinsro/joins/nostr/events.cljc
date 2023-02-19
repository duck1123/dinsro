(ns dinsro.joins.nostr.events
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   #?(:clj [dinsro.queries.nostr.event-tags :as q.n.event-tags])
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/events.clj][Event Actions]]
;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../ui/nostr/events.cljs][Event UI]]

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

(defattr tags ::tags :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::tags [::m.n.event-tags/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} params]
     (log/info :tags/starting {:params params :query-params query-params})
     (if-let [event-id (::m.n.events/id query-params)]
       (let [ids #?(:clj  (q.n.event-tags/find-by-event event-id)
                    :cljs (do (comment event-id) []))]
         {::tags (m.n.events/idents ids)})
       #?(:clj (throw (RuntimeException. "No pubkey supplied"))
          :cljs (throw (js/Error. "No pubkey supplied")))))})

(defattr tag-count ::tag-count :int
  {ao/identities #{::m.n.events/id}
   ao/pc-input   #{::m.n.events/id}
   ao/pc-resolve
   (fn [_env params]
     (log/info :event-count/starting {:params params})
     (let [event-id (::m.n.events/id params)
           tags   #?(:clj  (q.n.event-tags/find-by-event event-id)
                     :cljs (do (comment event-id) []))]
       {::tag-count (count tags)}))})

(def attributes [admin-index index tags tag-count])
