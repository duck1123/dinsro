(ns dinsro.joins.core.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.blocks :as m.core-blocks]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.peers :as m.core-peers]
   [dinsro.model.core.tx :as m.core-tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.core.wallets :as m.wallets]
   #?(:clj [dinsro.queries.core.blocks :as q.core-blocks])
   #?(:clj [dinsro.queries.core.nodes :as q.core-nodes])
   #?(:clj [dinsro.queries.core.peers :as q.core-peers])
   #?(:clj [dinsro.queries.core.tx :as q.core-tx])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.core.wallets :as q.wallets])
   [dinsro.specs]))

(defattr index ::m.core-nodes/index :ref
  {ao/cardinality :many
   ao/target      ::m.core-nodes/id
   ao/pc-output   [{::m.core-nodes/index [::m.core-nodes/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.core-nodes/index-ids) :cljs [])]
       {::m.core-nodes/index (m.core-nodes/idents ids)}))})

(defattr blocks ::m.core-nodes/blocks :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-nodes/id}
   ao/pc-output   [{::m.core-nodes/blocks [::m.core-blocks/id]}]
   ao/target      ::m.core-blocks/id
   ao/pc-resolve
   (fn [_env {::m.core-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.core-blocks/find-by-node id) :cljs []) [])]
       {::m.core-nodes/blocks (m.core-blocks/idents ids)}))})

(defattr ln-nodes ::m.core-nodes/ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-nodes/id}
   ao/pc-output   [{::m.core-nodes/ln-nodes [::m.ln.nodes/id]}]
   ao/target      ::m.ln.nodes/id
   ao/pc-resolve
   (fn [_env {::m.core-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.nodes/find-by-core-node id) :cljs []) [])]
       {::m.core-nodes/ln-nodes (m.ln.nodes/idents ids)}))})

(defattr peers ::m.core-nodes/peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-nodes/id}
   ao/pc-output   [{::m.core-nodes/peers [::m.core-peers/id]}]
   ao/target      ::m.core-peers/id
   ao/pc-resolve
   (fn [_env {::m.core-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.core-peers/find-by-core-node id) :cljs []) [])]
       {::m.core-nodes/peers (m.core-peers/idents ids)}))})

(defattr transactions ::m.core-nodes/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-nodes/id}
   ao/pc-output   [{::m.core-nodes/transactions [::m.core-tx/id]}]
   ao/target      ::m.core-tx/id
   ao/pc-resolve
   (fn [_env {::m.core-nodes/keys [id]}]
     (let [ids (if id  #?(:clj (q.core-tx/find-by-node id) :cljs []) [])]
       {::m.core-nodes/transactions (m.core-tx/idents ids)}))})

(defattr wallets ::m.core-nodes/wallets :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.core-nodes/id}
   ao/pc-output   [{::m.core-nodes/wallets [::m.wallets/id]}]
   ao/target      ::m.wallets/id
   ao/pc-resolve
   (fn [_env {::m.core-nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.wallets/find-by-core-node id) :cljs []) [])]
       {::m.core-nodes/wallets (m.wallets/idents ids)}))})

(def attributes
  [index
   blocks
   ln-nodes
   peers
   transactions
   wallets])
