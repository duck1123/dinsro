(ns dinsro.joins.nostr.requests
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.nostr.requests :as a.n.requests])
   [dinsro.joins :as j]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.filters :as q.n.filters])
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/requests.cljc]]
;; [[../../queries/nostr/requests.clj]]

(def join-info
  (merge
   {:idents m.n.requests/idents}
   #?(:clj {:indexer q.n.requests/index-ids
            :counter q.n.requests/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.requests/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.requests/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.requests/id
   ao/pc-output [{::index [:total {:results [::m.n.requests/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr filters ::filters :ref
  {ao/target    ::m.n.filters/id
   ao/pc-input  #{::m.n.requests/id}
   ao/pc-output [{::filters [::m.n.filters/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :filters/starting {:query-params query-params :props props})
     (let [request-id (::m.n.requests/id props)
           ids        #?(:clj (if request-id (q.n.filters/find-by-request request-id) [])
                         :cljs (do (comment request-id) []))]
       (log/trace :filters/finished {:ids ids})
       {::filters (m.n.filters/idents ids)}))})

(defattr filter-count ::filter-count :int
  {ao/identities #{::m.n.requests/id}
   ao/pc-input #{::filters}
   ao/pc-resolve (fn [_ {::keys [filters]}] {::filter-count (count filters)})})

(defattr runs ::runs :ref
  {ao/target    ::m.n.runs/id
   ao/pc-input  #{::m.n.requests/id}
   ao/pc-output [{::runs [::m.n.runs/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :runs/starting {:query-params query-params :props props})
     (let [request-id (::m.n.requests/id props)
           ids        #?(:clj (q.n.runs/find-by-request request-id)
                         :cljs (do (comment request-id) []))]
       (log/trace :runs/finished {:ids ids})
       {::runs (m.n.runs/idents ids)}))})

(defattr run-count ::run-count :int
  {ao/pc-input   #{::runs}
   ao/pc-resolve (fn [_ {::keys [runs]}] {::run-count (count runs)})})

(defattr query-string ::query-string :string
  {ao/identities #{::m.n.requests/id}
   ao/pc-input   #{::m.n.requests/id}
   ao/pc-resolve
   (fn [_ {::m.n.requests/keys [id]}]
     {::query-string #?(:clj (a.n.requests/get-query-string id)
                        :cljs (do (comment id) ""))})})

(def attributes [admin-index index filters filter-count query-string run-count runs])
