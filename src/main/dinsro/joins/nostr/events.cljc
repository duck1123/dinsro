(ns dinsro.joins.nostr.events
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   #?(:clj [dinsro.queries.nostr.event-tags :as q.n.event-tags])
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   #?(:clj [dinsro.queries.nostr.witnesses :as q.n.witnesses])
   #?(:clj [dinsro.specs :as ds])
   [lambdaisland.glogc :as log]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as transform]))

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
   ao/pc-input  #{::m.n.events/id}
   ao/pc-output [{::tags [::m.n.event-tags/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} params]
     (log/info :tags/starting {:params params :query-params query-params})
     (if-let [event-id (or (::m.n.events/id query-params) (::m.n.events/id params))]
       (let [ids #?(:clj  (q.n.event-tags/find-by-parent event-id)
                    :cljs (do (comment event-id) []))]
         {::tags (m.n.event-tags/idents ids)})
       (throw (ex-info "No event-id supplied" {}))))})

(defattr tag-count ::tag-count :int
  {ao/pc-input   #{::tags}
   ao/pc-resolve (fn [_ {::keys [tags]}] {::tag-count (count tags)})})

(defattr content-hiccup ::content-hiccup :string
  {ao/pc-input  #{::m.n.events/content}
   ao/pc-output [::content-hiccup]
   ao/pc-resolve
   (fn [_env {::m.n.events/keys [content]}]
     (let [ast            (md/parse content)
           content-hiccup (transform/->hiccup ast)]
       {::content-hiccup content-hiccup}))})

(defattr created-date ::created-date :inst
  {ao/pc-input  #{::m.n.events/created-at}
   ao/pc-output [::created-date]
   ao/pc-resolve
   (fn [_env {::m.n.events/keys [created-at]}]
     (let [created-date #?(:clj (ds/ms->inst (* created-at 1000))
                           :cljs (do (comment created-at) nil))]
       {::created-date created-date}))})

(defattr witness-count ::witness-count :int
  {ao/pc-input  #{::m.n.events/id}
   ao/pc-output [::witness-count]
   ao/pc-resolve
   (fn [_env props]
     (let [n #?(:clj (q.n.witnesses/count-ids props)
                :cljs (do (comment props) 0))]
       {::witness-count n}))})

(defattr witnesses ::witnesses :ref
  {ao/pc-input  #{::m.n.events/id}
   ao/target    ::m.n.witnesses/id
   ao/pc-output [{::witnesses [::m.n.witnesses/id]}]
   ao/pc-resolve
   (fn [_env props]
     (let [ids #?(:clj (q.n.witnesses/index-ids props)
                  :cljs (do (comment props) []))]
       {::witnesses (m.n.witnesses/idents ids)}))})

(def attributes
  [admin-index index tags tag-count
   content-hiccup created-date
   witness-count witnesses])
