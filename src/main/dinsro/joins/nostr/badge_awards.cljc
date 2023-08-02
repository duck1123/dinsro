(ns dinsro.joins.nostr.badge-awards
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   #?(:clj [dinsro.queries.nostr.badge-awards :as q.n.badge-awards])))

;; [[../../queries/nostr/badge_awards.clj]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/badge_awards_notebook.clj]]

(def join-info
  (merge
   {:idents m.n.badge-awards/idents}
   #?(:clj {:indexer q.n.badge-awards/index-ids
            :counter q.n.badge-awards/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.badge-awards/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.badge-awards/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.badge-awards/id
   ao/pc-output [{::index [:total {:results [::m.n.badge-awards/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
