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

(defattr index ::index :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::index [::m.users/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj (q.users/index-ids) :cljs [])]
       {::index (m.users/idents ids)}))})

(defattr index-by-pubkey ::index-by-pubkey :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::index-by-pubkey [::m.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (if-let [pubkey-id (::m.n.pubkeys/id query-params)]
       (let [ids #?(:clj (q.users/find-by-pubkey-id pubkey-id) :cljs [])]
         (comment env query-params pubkey-id)
         {::index-by-pubkey (m.users/idents ids)})
       #?(:clj (throw (RuntimeException. "Missing pubkey"))
          :cljs (throw (js/Error. "Missing pubkey")))))})

(defattr ln-nodes ::m.users/ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/ln-nodes [::m.ln.nodes/id]}]
   ao/target      ::m.ln.nodes/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     {::m.users/ln-nodes
      (let [ids #?(:clj (and id (q.ln.nodes/find-by-user id))
                   :cljs (do (comment id) []))]
        (map (fn [id] {::m.ln.nodes/id id}) ids))})})

(defattr transactions ::m.users/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-user id)]
                  {::m.users/transactions (map (fn [id] {::m.transactions/id id}) transaction-ids)})
          :cljs {::m.users/transactions []})
       {::m.users/transactions []}))})

(defattr wallets ::m.users/wallets :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/walllets [::m.c.wallets/id]}]
   ao/target      ::m.c.wallets/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (let [ids (if id
                 #?(:clj (q.c.wallets/find-by-user id) :cljs [])
                 [])]
       {::m.users/wallets (m.c.wallets/idents ids)}))})

(def attributes [accounts categories index index-by-pubkey ln-nodes transactions wallets])
