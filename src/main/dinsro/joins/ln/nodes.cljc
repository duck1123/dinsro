(ns dinsro.joins.ln.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   #?(:clj [dinsro.queries.core.transactions :as q.c.transactions])
   #?(:clj [dinsro.queries.ln.channels :as q.ln.channels])
   #?(:clj [dinsro.queries.ln.invoices :as q.ln.invoices])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.ln.payments :as q.ln.payments])
   #?(:clj [dinsro.queries.ln.payreqs :as q.ln.payreqs])
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   [dinsro.specs]))

;; [../../queries/ln/nodes.clj]

(def model-key ::m.ln.nodes/id)

(def join-info
  (merge
   {:idents m.ln.nodes/idents}
   #?(:clj {:indexer q.ln.nodes/index-ids
            :counter q.ln.nodes/count-ids})))

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::admin-flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::admin-index [:total {:results [::m.ln.nodes/id]}]}]
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
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::index [:total {:results [::m.ln.nodes/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr channels ::channels :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::channels [::m.ln.channels/id]}]
   ao/target      ::m.ln.channels/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.channels/index-ids {model-key id}) :cljs []) [])]
       {::channels (m.ln.channels/idents ids)}))})

(defattr channel-count ::channel-count :number
  {ao/pc-input   #{::channels}
   ao/pc-resolve (fn [_ {::keys [channels]}] {::channel-count (count channels)})})

(defattr invoices ::invoices :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::invoices [::m.ln.invoices/id]}]
   ao/target      ::m.ln.invoices/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.invoices/index-ids {model-key id}) :cljs []) [])]
       {::invoices (m.ln.invoices/idents ids)}))})

(defattr invoice-count ::invoice-count :number
  {ao/pc-input   #{::invoices}
   ao/pc-resolve (fn [_ {::keys [invoices]}] {::invoice-count (count invoices)})})

(defattr payments ::payments :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::payments [::m.ln.payments/id]}]
   ao/target      ::m.ln.payments/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payments/index-ids {model-key id}) :cljs []) [])]
       {::payments (m.ln.payments/idents ids)}))})

(defattr payment-count ::payment-count :number
  {ao/pc-input   #{::payments}
   ao/pc-resolve (fn [_ {::keys [payments]}] {::payment-count (count payments)})})

(defattr payreqs ::payreqs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::payreqs [::m.ln.payreqs/id]}]
   ao/target      ::m.ln.payreqs/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payreqs/index-ids {model-key id}) :cljs []) [])]
       {::payreqs (m.ln.payreqs/idents ids)}))})

(defattr peers ::peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/index-ids {model-key id}) :cljs []) [])]
       {::peers (m.ln.peers/idents ids)}))})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::transactions [::m.c.transactions/id]}]
   ao/target      ::m.c.transactions/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.transactions/index-ids {model-key id}) :cljs []) [])]
       {::transactions (m.c.transactions/idents (take 3 ids))}))})

(def attributes
  [admin-flat-index
   admin-index
   index
   channel-count
   channels
   flat-index
   invoice-count
   invoices
   payment-count
   payments
   payreqs
   peers
   transactions])
