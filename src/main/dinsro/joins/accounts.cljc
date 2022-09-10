(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(comment ::m.c.wallets/_)

(defattr transactions ::m.accounts/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::m.accounts/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     #?(:clj  (if id
                (let [ids (q.transactions/find-by-account id)]
                  {::m.accounts/transactions (map (fn [id] {::m.transactions/id id}) ids)})
                {::m.accounts/transactions []})
        :cljs (comment id)))})

;; "All accounts regardless of user"
(defattr admin-index ::m.accounts/admin-index :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/admin-index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (let [ids (if user-id #?(:clj (q.accounts/index-ids) :cljs []) [])]
       {::m.accounts/admin-index (m.accounts/idents ids)}))})

;; "All accounts belonging to authenticated user"
(defattr index ::m.accounts/index :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/index [::m.accounts/id]}]
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
       {::m.accounts/index (m.accounts/idents ids)}))})

(def attributes
  [transactions
   index
   admin-index])
