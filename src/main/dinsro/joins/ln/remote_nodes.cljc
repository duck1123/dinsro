(ns dinsro.joins.ln.remote-nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   #?(:clj [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.ln.remote-nodes/idents}
   #?(:clj {:indexer q.ln.remote-nodes/index-ids
            :counter q.ln.remote-nodes/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.remote-nodes/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.remote-nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.remote-nodes/id
   ao/pc-output [{::index [:total {:results [::m.ln.remote-nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr peers ::peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.remote-nodes/id}
   ao/pc-output   [{::peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.remote-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/find-by-remote-node id) :cljs []) [])]
       {::peers (m.ln.peers/idents ids)}))})

(def attributes [admin-index index peers])
