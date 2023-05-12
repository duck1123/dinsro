(ns dinsro.joins.nostr.connections
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.connections :as q.n.connections])
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [lambdaisland.glogc :as log]))

(def join-info
  (merge
   {:idents m.n.connections/idents}
   #?(:clj {:indexer q.n.connections/index-ids
            :counter q.n.connections/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.connections/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.connections/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.connections/id
   ao/pc-output [{::index [:total {:results [::m.n.connections/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

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

(def attributes [admin-index index run-count runs])
