(ns dinsro.joins.nostr.filters
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   #?(:clj [dinsro.queries.nostr.filter-items :as q.n.filter-items])
   #?(:clj [dinsro.queries.nostr.filters :as q.n.filters])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

#?(:cljs (comment ::m.n.filter-items/_))

(def join-info
  (merge
   {:idents m.n.filters/idents}
   #?(:clj {:indexer q.n.filters/index-ids
            :counter q.n.filters/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.filters/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.filters/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.filters/id
   ao/pc-output [{::index [:total {:results [::m.n.filters/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

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

(def attributes [admin-index index items item-count])
