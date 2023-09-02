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

(def model-key ::m.ln.remote-nodes/id)

(def join-info
  (merge
   {:idents m.ln.remote-nodes/idents}
   #?(:clj {:indexer q.ln.remote-nodes/index-ids
            :counter q.ln.remote-nodes/count-ids})))

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::admin-flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.remote-nodes/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.remote-nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr flat-index ::flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::flat-index (j/make-flat-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.ln.remote-nodes/id
   ao/pc-output [{::index [:total {:results [::m.ln.remote-nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr peers ::peers :ref
  {ao/cardinality :many
   ao/pc-input    #{model-key}
   ao/pc-output   [{::peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.remote-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/index-ids {model-key id}) :cljs []) [])]
       {::peers (m.ln.peers/idents ids)}))})

(def attributes [admin-flat-index admin-index flat-index index peers])
