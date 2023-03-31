(ns dinsro.joins.nostr.connections
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.connections :as q.n.connections])
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.connections/id
   ao/pc-output [{::index [::m.n.connections/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [{relay-id ::m.n.relays/id} query-params
           ids                        #?(:clj  (cond
                                                 relay-id (q.n.connections/find-by-relay relay-id)
                                                 :else    (q.n.connections/index-ids))
                                         :cljs (do
                                                 (comment relay-id)
                                                 []))]
       {::index (m.n.connections/idents ids)}))})

(defattr runs ::runs :ref
  {ao/target    ::m.n.runs/id
   ao/pc-input  #{::m.n.connections/id}
   ao/pc-output [{::runs [::m.n.runs/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :runs/starting {:query-params query-params :props props})
     (let [connection-id (::m.n.connections/id props)
           ids        #?(:clj (q.n.runs/find-by-connection connection-id)
                         :cljs (do
                                 (comment connection-id)
                                 []))]
       (log/trace :runs/finished {:ids ids})
       {::runs (m.n.runs/idents ids)}))})

(defattr run-count ::run-count :int
  {ao/pc-input   #{::runs}
   ao/pc-resolve (fn [_ {::keys [runs]}] {::run-count (count runs)})})

(def attributes [index run-count runs])
