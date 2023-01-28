(ns dinsro.joins.debits
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.debits :as q.debits])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; "All debits regardless of user"
(defattr admin-index ::admin-index :ref
  {ao/target    ::m.debits/id
   ao/pc-output [{::admin-index [::m.debits/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (log/info :admin-index/starting {})
     (let [ids (if user-id #?(:clj (q.debits/index-ids) :cljs []) [])]
       {::admin-index (m.debits/idents ids)}))})

;; "All debits belonging to authenticated user"
(defattr index ::index :ref
  {ao/target    ::m.debits/id
   ao/pc-output [{::index [::m.debits/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id
         :keys   [query-params]
         :as     _env} _]
     (log/info :index/starting {})
     (let [{transaction-id ::m.transactions/id
            account-id     ::m.accounts/id} query-params]
       (log/info :index/starting {:user-id        user-id
                                  :transaction-id transaction-id
                                  :account-id     account-id
                                  :query-prams    query-params})
       (let [ids #?(:clj
                    (if transaction-id
                      (q.debits/find-by-transaction transaction-id)
                      (if account-id
                        (q.debits/find-by-account account-id)
                        (if user-id (q.debits/find-by-user user-id) [])))
                    :cljs [])]
         {::index (m.debits/idents ids)})))})

(def attributes [index admin-index])
