(ns dinsro.joins.instances
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.instances :as m.instances]
   #?(:clj [dinsro.queries.instances :as q.instances])))

;; [[../actions/instances.clj]]
;; [[../model/instances.cljc]]
;; [[../mutations/instances.cljc]]
;; [[../queries/instances.clj]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(def join-info
  (merge
   {:idents m.instances/idents}
   #?(:clj {:indexer q.instances/index-ids
            :counter q.instances/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.instances/id
   ao/pc-output  [{::admin-index [:total {:results [::m.instances/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.instances/id
   ao/pc-output [{::index [:total {:results [::m.instances/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(def attributes
  [admin-index index])
