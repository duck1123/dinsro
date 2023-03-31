(ns dinsro.joins.nostr.filters
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.queries.nostr.filter-items :as q.n.filter-items])
   #?(:clj [dinsro.queries.nostr.filters :as q.n.filters])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

#?(:cljs (comment ::m.n.filter-items/_))

(defattr index ::index :ref
  {ao/target    ::m.n.filters/id
   ao/pc-output [{::index [::m.n.filters/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [request-id (::m.n.requests/id query-params)
           ids        #?(:clj (cond
                                request-id (q.n.filters/find-by-request request-id)
                                :else      (q.n.filters/index-ids))
                         :cljs (do
                                 (comment request-id)
                                 []))]
       (log/trace :index/finished {:ids ids})
       {::index (m.n.filters/idents ids)}))})

(defattr items ::items :ref
  {ao/target     ::m.n.filter-items/id
   ao/identities #{::m.n.filters/id}
   ao/pc-input   #{::m.n.filters/id}
   ao/pc-output  [{::items [::m.n.filter-items/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} {::m.n.filters/keys [id] :as props}]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids #?(:clj (q.n.filter-items/find-by-filter id)
                  :cljs (do (comment id) []))]
       (log/trace :index/finished {:ids ids})
       {::items (m.n.filter-items/idents ids)}))})

(defattr item-count ::item-count :int
  {ao/pc-input   #{::items}
   ao/pc-output  [::item-count]
   ao/pc-resolve (fn [_ {::keys [items]}] {::item-count (count items)})})

(def attributes [index items item-count])
