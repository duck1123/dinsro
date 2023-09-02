(ns dinsro.joins.core.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   #?(:clj [dinsro.queries.core.blocks :as q.c.blocks])
   #?(:clj [dinsro.queries.core.nodes :as q.c.nodes])
   #?(:clj [dinsro.queries.core.peers :as q.c.peers])
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   [dinsro.specs]))

(def model-key ::m.c.nodes/id)

(def join-info
  (merge
   {:idents m.c.nodes/idents}
   #?(:clj {:indexer q.c.nodes/index-ids
            :counter q.c.nodes/count-ids})))

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::admin-flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.nodes/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.nodes/id]}]}]
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
  {ao/target    ::m.c.nodes/id
   ao/pc-output [{::index [:total {:results [::m.c.nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr blocks ::blocks :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::blocks [::m.c.blocks/id]}]
   ao/target      ::m.c.blocks/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.blocks/index-ids {model-key id}) :cljs []) [])]
       {::blocks (m.c.blocks/idents ids)}))})

(defattr ln-nodes ::ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::ln-nodes [::m.ln.nodes/id]}]
   ao/target      ::m.ln.nodes/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.nodes/index-ids {model-key id}) :cljs []) [])]
       {::ln-nodes (m.ln.nodes/idents ids)}))})

(defattr peers ::peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::peers [::m.c.peers/id]}]
   ao/target      ::m.c.peers/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.peers/index-ids {model-key id}) :cljs []) [])]
       {::peers (m.c.peers/idents ids)}))})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::transactions [::m.c.transactions/id]}]
   ao/target      ::m.c.transactions/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id  #?(:clj (q.c.transactions/index-ids {model-key id}) :cljs []) [])]
       {::transactions (m.c.transactions/idents ids)}))})

(defattr wallets ::wallets :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.c.nodes/id}
   ao/pc-output   [{::wallets [::m.c.wallets/id]}]
   ao/target      ::m.c.wallets/id
   ao/pc-resolve
   (fn [_env {::m.c.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.wallets/index-ids {model-key id}) :cljs []) [])]
       {::wallets (m.c.wallets/idents ids)}))})

(def attributes
  [admin-flat-index
   admin-index
   flat-index
   index
   blocks
   ln-nodes
   peers
   transactions
   wallets])
