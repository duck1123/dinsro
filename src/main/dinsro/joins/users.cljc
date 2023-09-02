(ns dinsro.joins.users
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins :as j]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]))

;; [[../actions/users.clj]]
;; [[../model/users.cljc]]
;; [[../queries/users.clj]]
;; [[../ui/users.cljs]]

(def model-key ::m.users/id)

(def join-info
  (merge
   {:idents m.users/idents}
   #?(:clj {:indexer q.users/index-ids
            :counter q.users/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.users/id
   ao/pc-output  [{::admin-index [:total {:results [::m.users/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::admin-flat-index [::m.users/id]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr index ::index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::index [:total {:results [::m.users/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr account-count ::account-count :number
  {ao/pc-input   #{::accounts}
   ao/pc-resolve (fn [_ {::keys [accounts]}] {::account-count (count accounts)})})

(defattr accounts ::accounts :ref
  {ao/cardinality :many
   ao/target      ::m.accounts/id
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     #?(:clj  (let [account-ids (if id (q.accounts/find-by-user id) [])]
                {::accounts (m.accounts/idents account-ids)})
        :cljs (comment id)))})

(defattr category-count ::category-count :number
  {ao/pc-input   #{::categories}
   ao/pc-resolve (fn [_ {::keys [categories]}] {::category-count (count categories)})})

(defattr categories ::categories :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::categories [::m.categories/id]}]
   ao/target      ::m.categories/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (let [category-ids #?(:clj  (if id (q.categories/find-by-user id) [])
                           :cljs (do (comment id) []))]
       {::categories (m.categories/idents category-ids)}))})

(defattr record-count
  "Count of users in system"
  ::record-count :number
  {ao/pc-output [::record-count]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [n #?(:clj (q.users/count-ids query-params)
                :cljs (do (comment query-params) 0))]
       {::record-count n}))})

(defattr flat-index ::flat-index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::flat-index [::m.users/id]}]
   ao/pc-resolve
   (fn [env props]
     {::flat-index (j/make-flat-indexer join-info env props)})})

;; Count of ln nodes associated with user
(defattr ln-node-count ::ln-node-count :number
  {ao/pc-input   #{::ln-nodes}
   ao/pc-resolve (fn [_ {::keys [ln-nodes]}] {::ln-node-count (count ln-nodes)})})

(defattr ln-nodes ::ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::ln-nodes [::m.ln.nodes/id]}]
   ao/target      ::m.ln.nodes/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (let [ids #?(:clj (and id (q.ln.nodes/find-by-user id))
                  :cljs (do (comment id) []))]
       {::ln-nodes (m.ln.nodes/idents ids)}))})

(defattr transaction-count ::transaction-count :number
  {ao/pc-input   #{::transactions}
   ao/pc-resolve (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (if id
       (let [ids #?(:clj (q.transactions/index-ids {model-key id})
                    :cljs [])]
         {::transactions (m.transactions/idents ids)})
       (throw (ex-info "ID not provided" {}))))})

(defattr wallet-count ::wallet-count :number
  {ao/pc-input   #{::wallets}
   ao/pc-resolve (fn [_ {::keys [wallets]}] {::wallet-count (count wallets)})})

(defattr wallets ::wallets :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::wallets [::m.c.wallets/id]}]
   ao/target      ::m.c.wallets/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (let [ids (if id
                 #?(:clj (q.c.wallets/find-by-user id) :cljs [])
                 [])]
       {::wallets (m.c.wallets/idents ids)}))})

(def attributes [account-count
                 accounts
                 admin-flat-index
                 admin-index
                 category-count
                 categories
                 flat-index
                 record-count
                 index
                 ln-node-count
                 ln-nodes
                 transaction-count
                 transactions
                 wallet-count
                 wallets])
