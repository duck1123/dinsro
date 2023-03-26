(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.debits :as q.debits])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_ ::m.rate-sources/_)

(defattr debit-count ::debit-count :ref
  {ao/pc-input #{::debits}
   ao/pc-resolve (fn [_ {::keys [debits]}] {::debit-count (count debits)})})

(defattr debits ::debits :ref
  {ao/cardinality :many
   ao/pc-input #{::m.accounts/id}
   ao/pc-output [{::debits [::m.debits/id]}]
   ao/target ::m.debits/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     (let [ids #?(:clj (q.debits/find-by-account id)
                  :cljs (do (comment id) []))]
       {::debits (m.debits/idents ids)}))})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     #?(:clj  (if id
                (let [ids (q.transactions/find-by-account id)]
                  {::transactions (m.transactions/idents ids)})
                {::transactions []})
        :cljs (comment id)))})

(defattr transaction-count ::transaction-count :number
  {ao/pc-input   #{::transactions}
   ao/pc-resolve (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

;; "All accounts regardless of user"
(defattr admin-index ::admin-index :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::admin-index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (let [ids (if user-id #?(:clj (q.accounts/index-ids) :cljs []) [])]
       {::admin-index (m.accounts/idents ids)}))})

(defn do-index
  [{:keys    [query-params] :as env
    actor-id ::m.users/id} params]
  (log/info :index/starting {:query-params query-params :params params})
  (comment env)
  (let [{wallet-id      ::m.c.wallets/id
         user-id        ::m.users/id
         currency-id    ::m.currencies/id
         rate-source-id ::m.rate-sources/id} query-params
        ids
        #?(:clj
           (cond
             rate-source-id        (q.accounts/find-by-rate-source rate-source-id)
             wallet-id             (q.accounts/find-by-wallet wallet-id)
             (or user-id actor-id) (q.accounts/find-by-user (or user-id actor-id))
             currency-id           (q.accounts/find-by-currency currency-id)
             :else                 (do (log/warn :index/no-user {}) []))
           :cljs
           (do
             (comment wallet-id user-id currency-id rate-source-id actor-id)
             []))]
    {::index (m.accounts/idents ids)}))

;; "All accounts belonging to authenticated user"
(defattr index ::index :ref
  {ao/target     ::m.accounts/id
   ao/pc-output  [{::index [::m.accounts/id]}]
   ao/pc-resolve #(do-index %1 %2)})

(def attributes
  [admin-index debits debit-count index transaction-count transactions])
