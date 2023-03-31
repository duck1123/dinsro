(ns dinsro.joins.nostr.witnesses
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   ;; #?(:clj [dinsro.actions.nostr.witnesses :as a.n.witnesses])
   ;; [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   ;; #?(:clj [dinsro.queries.nostr.filters :as q.n.filters])
   #?(:clj [dinsro.queries.nostr.witnesses :as q.n.witnesses])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.n.witnesses/id
   ao/pc-output [{::index [::m.n.witnesses/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} props]
     (log/info :index/starting {:query-params query-params :props props})
     (let [ids #?(:clj (q.n.witnesses/index-ids)
                  :cljs [])]
       (log/trace :index/finished {:ids ids})
       {::index (m.n.witnesses/idents ids)}))})

(def attributes [index])
