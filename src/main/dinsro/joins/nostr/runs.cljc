(ns dinsro.joins.nostr.runs
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.runs :as m.n.runs]
   #?(:clj [dinsro.queries.nostr.runs :as q.n.runs])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.runs/id
   ao/pc-output [{::index [::m.n.runs/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids #?(:clj (q.n.runs/index-ids)
                  :cljs [])]
       (log/trace :index/finished {:ids ids})
       {::index (m.n.runs/idents ids)}))})

(def attributes [index])
