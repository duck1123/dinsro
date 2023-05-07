(ns dinsro.joins.users
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]))

;; [[../actions/users.clj][User Actions]]
;; [[../model/users.cljc][Users Model]]
;; [[../queries/users.clj][User Queries]]
;; [[../ui/users.cljs][Users UI]]

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

;; TODO: The current user must have the admin role
(defattr admin-index ::admin-index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::admin-index [::m.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [ids #?(:clj (q.users/index-ids query-params)
                  :cljs (do (comment query-params) []))]
       {::admin-index (m.users/idents ids)}))})

;; Paginated list of users
;; TODO: This should only show the authenticated user
(defattr index ::index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::index [::m.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [ids #?(:clj (q.users/index-ids query-params)
                  :cljs (do (comment query-params) []))]
       {::index (m.users/idents ids)}))})

;; Count of users in system
(defattr record-count ::record-count :number
  {ao/pc-output [::record-count]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [n #?(:clj (q.users/count-ids query-params)
                :cljs (do (comment query-params) 0))]
       {::record-count n}))})

;; Deprecated
(defattr index-by-pubkey ::index-by-pubkey :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::index-by-pubkey [::m.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (if-let [pubkey-id (::m.n.pubkeys/id query-params)]
       (let [ids #?(:clj (q.users/find-by-pubkey-id pubkey-id) :cljs [])]
         (comment env query-params pubkey-id)
         {::index-by-pubkey (m.users/idents ids)})
       (throw (ex-info "Missing pubkey" {}))))})

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
       (let [ids #?(:clj (q.transactions/find-by-user id)
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
                 admin-index
                 category-count
                 categories
                 record-count
                 index
                 index-by-pubkey
                 ln-node-count
                 ln-nodes
                 transaction-count
                 transactions
                 wallet-count
                 wallets])
