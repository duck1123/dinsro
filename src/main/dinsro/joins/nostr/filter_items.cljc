(ns dinsro.joins.nostr.filter-items
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.queries.nostr.filter-items :as q.n.filter-items])
   #?(:clj [dinsro.queries.nostr.requests :as q.n.requests])
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.filter-items/id
   ao/pc-output [{::index [::m.n.filter-items/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} params]
     (log/info :index/starting {:query-params query-params :params params})
     (let [{filter-id ::m.n.filters/id} query-params]
       (log/info :index/starting {:filter-id filter-id})
       (let [ids #?(:clj (cond
                           filter-id (q.n.filter-items/find-by-filter filter-id)
                           :else     (q.n.filter-items/index-ids))
                    :cljs (do
                            (comment filter-id)
                            []))]
         (log/trace :index/finished {:ids ids})
         {::index (m.n.filter-items/idents ids)})))})

(defattr request ::request :ref
  {ao/target    ::m.n.requests/id
   ao/pc-input  #{::m.n.filter-items/id}
   ao/pc-output [{::request [::m.n.requests/id]}]
   ao/pc-resolve
   (fn [_ {::m.n.filter-items/keys [id]}]
     (let [request-id #?(:clj (q.n.requests/find-by-filter-item id)
                         :cljs (do (comment id) nil))]
       {::request (when request-id (m.n.requests/ident request-id))}))})

(def attributes [index request])
