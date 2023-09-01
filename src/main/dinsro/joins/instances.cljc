(ns dinsro.joins.instances
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.instances :as m.instances]
   [dinsro.model.nostr.connections :as m.n.connections]
   #?(:clj [dinsro.queries.instances :as q.instances])
   #?(:clj [dinsro.queries.nostr.connections :as q.n.connections])))

;; [[../actions/instances.clj]]
;; [[../model/instances.cljc]]
;; [[../mutations/instances.cljc]]
;; [[../queries/instances.clj]]
;; [[../ui/admin/instances.cljc]]
;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(def model-key ::m.instances/id)

(def join-info
  (merge
   {:idents m.instances/idents}
   #?(:clj {:indexer q.instances/index-ids
            :counter q.instances/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.instances/id
   ao/pc-output  [{::admin-index [:total {:results [model-key]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr connections ::connections :ref
  {ao/target    ::m.n.connections/id
   ao/pc-output [{::connections [:total {:results [model-key]}]}]
   ao/pc-resolve
   (fn [_env props]
     (let [id (model-key props)
           ids        #?(:clj (q.n.connections/index-ids {model-key id})
                         :cljs (do (comment id) []))]
       {::connections ((:idents join-info) ids)}))})

(defattr connection-count ::connection-count :int
  {ao/pc-input   #{::connections}
   ao/pc-resolve (fn [_ {::keys [connections]}] {::connection-count (count connections)})})

(defattr index ::index :ref
  {ao/target    model-key
   ao/pc-output [{::index [:total {:results [model-key]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr alive? ::alive? :ref
  {ao/target    model-key
   ao/pc-input #{::m.instances/last-heartbeat}
   ao/pc-output [::alive?]
   ao/pc-resolve
   (fn [_env props]
     (let [{::m.instances/keys [last-heartbeat]} props
           is-alive? #?(:clj (not (q.instances/expired? last-heartbeat))
                        :cljs (do (comment last-heartbeat) true))]
       {::alive? is-alive?}))})

(def attributes
  [admin-index index alive? connections connection-count])
