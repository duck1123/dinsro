(ns dinsro.joins.transactions
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.transactions/id
   ao/pc-output [{::admin-index [::m.transactions/id]}]
   ao/pc-resolve
   (fn [_ _]
     (let [ids #?(:clj (q.transactions/index-ids) :cljs [])]
       {::admin-index (m.transactions/idents ids)}))})

(defattr index ::index :ref
  {ao/target    ::m.transactions/id
   ao/pc-output [{::index [::m.transactions/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (log/info :index/starting {:query-params query-params})
     (comment env)
     (let [ids #?(:clj (let [user-id (a.authentication/get-user-id env)]
                         (q.transactions/find-by-user user-id))
                  :cljs [])]
       {::index (m.transactions/idents ids)}))})

(defattr user ::user :ref
  {ao/cardinality      :one
   ao/pc-input         #{::m.transactions/id}
   ao/target           ::m.users/id
   ao/pc-output        [{::user [::m.users/id]}]
   ao/pc-resolve
   (fn [_env {::m.transactions/keys [id]}]
     (let [user-id (if id #?(:clj (q.users/find-by-transaction id) :cljs nil) nil)]
       {::user (when user-id (m.users/ident user-id))}))
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(def attributes [admin-index index user])
