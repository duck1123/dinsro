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
   ao/pc-output [{::index [:total {:results [::m.n.events/id]}]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [ids #?(:clj (q.n.events/index-ids query-params)
                  :cljs (do
                          (comment query-params)
                          []))]
       (log/trace :index/finished {:ids ids})
       {::index {:total #?(:clj (q.n.events/count-ids query-params) :cljs 0)
                 :results (m.n.events/idents ids)}}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.events/id]}]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       {::admin-index {:total 21
                       :results (m.n.events/idents ids)}}))})

(defattr tags ::tags :ref
  {ao/target    ::m.n.event-tags/id
   ao/pc-output [{::tags [::m.n.event-tags/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} params]
     (log/info :tags/starting {:params params :query-params query-params})
     (if-let [event-id (::m.n.events/id query-params)]
       (let [ids #?(:clj  (q.n.event-tags/find-by-event event-id)
                    :cljs (do (comment event-id) []))]
         {::tags (m.n.events/idents ids)})
       (throw (ex-info "No pubkey supplied" {}))))})

(defattr tag-count ::tag-count :int
  {ao/pc-input   #{::tags}
   ao/pc-resolve (fn [_ {::keys [tags]}] {::tag-count (count tags)})})

(def attributes [admin-index index tags tag-count])
