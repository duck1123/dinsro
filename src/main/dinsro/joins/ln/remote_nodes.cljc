(ns dinsro.joins.ln.remote-nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   #?(:clj [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes])
   [dinsro.specs]))

(defattr index ::m.ln.remote-nodes/index :ref
  {ao/target    ::m.ln.remote-nodes/id
   ao/pc-output [{::m.ln.remote-nodes/index [::m.ln.remote-nodes/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.remote-nodes/index-ids) :cljs [])]
       {::m.ln.remote-nodes/index (m.ln.remote-nodes/idents ids)}))})

(defattr peers ::m.ln.remote-nodes/peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.remote-nodes/id}
   ao/pc-output   [{::m.ln.remote-nodes/peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.remote-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/find-ids-by-remote-node id) :cljs []) [])]
       {::m.ln.remote-nodes/peers (m.ln.peers/idents ids)}))})

(def attributes [index peers])
