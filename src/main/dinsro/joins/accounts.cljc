(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]))

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

(defattr admin-index ::m.accounts/admin-index :ref
  ;; "All accounts regardless of user"
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/admin-index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (let [ids (if user-id #?(:clj (q.accounts/index-ids) :cljs []) [])]
       {::m.accounts/admin-index (m.accounts/idents ids)}))})

(defattr index ::m.accounts/index :ref
  ;; "All accounts belonging to authenticated user"
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (let [ids (if user-id #?(:clj (q.accounts/find-by-user user-id) :cljs []) [])]
       {::m.accounts/index (m.accounts/idents ids)}))})

(def attributes
  [transactions
   index
   admin-index])
