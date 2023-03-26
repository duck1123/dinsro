(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_ ::m.rate-sources/_)

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

;; "All accounts belonging to authenticated user"
(defattr index ::index :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{:keys   [query-params] :as env} _]
     (log/info :index/starting {:query-params query-params})
     (comment env)
     (let [ids #?(:clj
                  (if-let [wallet-id (::m.c.wallets/id query-params)]
                    (q.accounts/find-by-wallet wallet-id)
                    (if-let [user-id (::m.users/id env)]
                      (q.accounts/find-by-user user-id)
                      (do
                        (log/warn :index/no-user {})
                        [])))
                  :cljs [])]
       {::index (m.accounts/idents ids)}))})

(defattr index-by-rate-source ::index-by-rate-source :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::index-by-rate-source [::m.accounts/id]}]
   ao/pc-resolve
   #?(:clj (fn [{:keys [query-params]} _]
             (log/info :index-by-rate-source/starting {:query-params query-params})
             (let [ids (if-let [rate-source-id (::m.rate-sources/id query-params)]
                         (q.accounts/find-by-rate-source rate-source-id)
                         [])]
               {::index-by-rate-source (m.accounts/idents ids)}))
      :cljs (fn [] {::index-by-rate-source []}))})

(def attributes
  [admin-index index index-by-rate-source
   transaction-count transactions])
