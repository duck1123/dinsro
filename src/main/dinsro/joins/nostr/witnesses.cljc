(ns dinsro.joins.nostr.witnesses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.witnesses :as q.n.witnesses])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [../../model/nostr/witnesses.cljc]
;; [../../queries/nostr/witnesses.clj]
;; [../../ui/admin/nostr/witnesses.cljs]
;; [../../ui/nostr/events/witnesses.cljs]

(defattr index ::index :ref
  {ao/target    ::m.n.witnesses/id
   ao/pc-output [{::index [:total {:results [::m.n.witnesses/id]}]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids   #?(:clj (q.n.witnesses/index-ids query-params)
                    :cljs [])
           total #?(:clj (q.n.witnesses/count-ids query-params) :cljs 0)]
       (log/trace :index/finished {:ids ids :total total})
       {::index {:total total :results (m.n.witnesses/idents ids)}}))})

(defattr relay ::relay :ref
  {ao/target           ::m.n.relays/id
   ao/pc-input         #{::m.n.witnesses/id}
   ao/pc-output        [{::relay [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env props]
     (let [witness-id (::m.n.witnesses/id props)
           relay-id   #?(:clj (q.n.relays/find-by-witness witness-id)
                         :cljs (do (comment witness-id) nil))]
       {::relay (when relay-id (m.n.relays/ident relay-id))}))
   ::report/column-EQL {::relay [::m.n.relays/id ::m.n.relays/address]}})

(def attributes [index relay])
