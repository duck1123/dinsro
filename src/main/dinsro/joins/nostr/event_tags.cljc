(ns dinsro.joins.nostr.event-tags
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   #?(:clj [dinsro.queries.nostr.event-tags :as q.n.event-tags])
   [dinsro.specs]))

;; [[../../model/nostr/event_tags.cljc][Event Tags Model]]

(def join-info
  (merge
   {:idents m.n.event-tags/idents}
   #?(:clj {:indexer q.n.event-tags/index-ids
            :counter q.n.event-tags/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.event-tags/id
   ao/pc-output [{::admin-index [:total {:results [::m.n.event-tags/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.n.event-tags/id
   ao/pc-output [{::index [:total {:results [::m.n.event-tags/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes [admin-index index])
