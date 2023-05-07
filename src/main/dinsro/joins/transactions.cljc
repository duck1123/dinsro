(ns dinsro.joins.transactions
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.debits :as q.debits])
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

(defattr debits ::debits :ref
  {ao/cardinality      :many
   ao/target           ::m.debits/id
   ao/pc-input         #{::m.transactions/id}
   ao/pc-output        [{::debits [::m.debits/id]}]
   ao/pc-resolve
   (fn [_ {transaction-id ::m.transactions/id}]
     (let [ids #?(:clj  (q.debits/index-ids {::m.transactions/id transaction-id})
                  :cljs (do
                          (comment transaction-id)
                          []))]
       {::debits (m.debits/idents ids)}))
   ::report/column-EQL {::debits [::m.debits/id ::m.debits/value]}})

(defattr positive-debits ::positive-debits :ref
  {ao/cardinality      :many
   ao/target           ::m.debits/id
   ao/pc-input         #{::m.transactions/id}
   ao/pc-output        [{::positive-debits [::m.debits/id]}]
   ao/pc-resolve
   (fn [_ {transaction-id ::m.transactions/id}]
     (let [ids #?(:clj  (q.debits/index-ids {::m.transactions/id transaction-id
                                             :positive? true})
                  :cljs (do
                          (comment transaction-id)
                          []))]
       {::positive-debits (m.debits/idents ids)}))
   ::report/column-EQL {:positive-:debits [::m.debits/id ::m.debits/value]}})

(defattr negative-debits ::negative-debits :ref
  {ao/cardinality      :many
   ao/target           ::m.debits/id
   ao/pc-input         #{::m.transactions/id}
   ao/pc-output        [{::negative-debits [::m.debits/id]}]
   ao/pc-resolve
   (fn [_ {transaction-id ::m.transactions/id}]
     (let [ids #?(:clj  (q.debits/index-ids {::m.transactions/id transaction-id
                                             :positive?          false})
                  :cljs (do
                          (comment transaction-id)
                          []))]
       {::negative-debits (m.debits/idents ids)}))
   ::report/column-EQL {::negative-debits [::m.debits/id ::m.debits/value]}})

(>def ::debit-count int?)
(defattr debit-count ::debit-count :number
  {ao/pc-input   #{::debits}
   ao/pc-resolve (fn [_ {::keys [debits]}] {::debit-count (count debits)})})

(defn do-index
  [{:keys [query-params] actor-id ::m.users/id} _]
  (log/info :index/starting {:actor-id actor-id :query-params query-params})
  (let [{account-id ::m.accounts/id} query-params
        ids #?(:clj
               (cond
                 account-id (q.transactions/find-by-account-and-user account-id actor-id)
                 :else      (q.transactions/find-by-user actor-id))
               :cljs (do
                       (comment actor-id account-id)
                       []))]
    {::index (m.transactions/idents ids)}))

(defattr index ::index :ref
  {ao/target    ::m.transactions/id
   ao/pc-output [{::index [::m.transactions/id]}]
   ao/pc-resolve #(do-index %1 %2)})

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

(def attributes [admin-index debit-count debits
                 positive-debits negative-debits
                 index user])
