(ns dinsro.joins.nostr.runs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/runs.cljc]]
;; [[../../queries/nostr/runs.clj]]

(defattr index ::index :ref
  {ao/target    ::m.n.runs/id
   ao/pc-output [{::index [:total {:results [::m.n.runs/id]}]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids #?(:clj (q.n.runs/index-ids query-params)
                  :cljs [])]
       (log/trace :index/finished {:ids ids})
       (let [idents       (m.n.runs/idents ids)
             record-count #?(:clj (q.n.runs/count-ids query-params) :cljs 0)]
         {::index {:total   record-count
                   :results idents}})))})

(defattr relay ::relay :ref
  {ao/target           ::m.n.relays/id
   ao/pc-input         #{::m.n.runs/id}
   ao/pc-output        [{::relay [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env props]
     (let [run-id (::m.n.runs/id props)
           relay-id   #?(:clj (q.n.relays/find-by-run run-id)
                         :cljs (do (comment run-id) nil))]
       {::relay (when relay-id (m.n.relays/ident relay-id))}))
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(def attributes [index relay])
