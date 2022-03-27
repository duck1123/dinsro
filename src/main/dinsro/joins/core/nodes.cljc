(ns dinsro.joins.core.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.peers :as q.c.peers])
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   [dinsro.specs]))

(defattr index ::m.c.nodes/index :ref
  {ao/cardinality :many
   ao/target      ::m.c.nodes/id
   ao/pc-output   [{::m.c.nodes/index [::m.c.nodes/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.nodes/index-ids) :cljs [])]
       {::m.c.nodes/index (m.c.nodes/idents ids)}))})

(defattr blocks ::m.c.nodes/blocks :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::m.c.nodes/blocks [::m.c.blocks/id]}]
   ao/target      ::m.c.blocks/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.blocks/find-by-node id) :cljs []) [])]
       {::m.c.nodes/blocks (m.c.blocks/idents ids)}))})

(defattr ln-nodes ::m.c.nodes/ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::m.c.nodes/ln-nodes [::m.ln.nodes/id]}]
   ao/target      ::m.ln.nodes/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.nodes/find-by-core-node id) :cljs []) [])]
       {::m.c.nodes/ln-nodes (m.ln.nodes/idents ids)}))})

(defattr peers ::m.c.nodes/peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::m.c.nodes/peers [::m.c.peers/id]}]
   ao/target      ::m.c.peers/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.peers/find-by-core-node id) :cljs []) [])]
       {::m.c.nodes/peers (m.c.peers/idents ids)}))})

(defattr transactions ::m.c.nodes/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::m.c.nodes/transactions [::m.c.tx/id]}]
   ao/target      ::m.c.tx/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id  #?(:clj (q.c.tx/find-by-node id) :cljs []) [])]
       {::m.c.nodes/transactions (m.c.tx/idents ids)}))})

(defattr wallets ::m.c.nodes/wallets :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::m.c.nodes/wallets [::m.c.wallets/id]}]
   ao/target      ::m.c.wallets/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.wallets/find-by-core-node id) :cljs []) [])]
       {::m.c.nodes/wallets (m.c.wallets/idents ids)}))})

(def attributes
  [index
   blocks
   ln-nodes
   peers
   transactions
   wallets])
