(ns dinsro.joins.transactions
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.debits :as q.debits])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]))

(def join-info
  (merge
   {:idents m.transactions/idents}
   #?(:clj {:indexer q.transactions/index-ids
            :counter q.transactions/count-ids})))

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.transactions/id
   ao/pc-output  [{::admin-index [:total {:results [::m.transactions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.transactions/id
   ao/pc-output [{::index [:total {:results [::m.transactions/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr debits ::debits :ref
  {ao/cardinality      :many
   ao/target           ::m.debits/id
   ao/pc-input         #{::m.transactions/id}
   ao/pc-output        [{::debits [::m.debits/id]}]
   ao/pc-resolve
   (fn [_ {transaction-id ::m.transactions/id}]
     (let [ids #?(:clj  (q.debits/index-ids {::m.transactions/id transaction-id})
                  :cljs (do (comment transaction-id) []))]
       {::debits (m.debits/idents ids)}))
   ::report/column-EQL {::debits [::m.debits/id ::m.debits/value]}})

(defattr positive-debits ::positive-debits :ref
  {ao/cardinality      :many
   ao/target           ::m.debits/id
   ao/pc-input         #{::m.transactions/id}
   ao/pc-output        [{::positive-debits [::m.debits/id]}]
   ao/pc-resolve
   (fn [_ {transaction-id ::m.transactions/id}]
     (let [ids    #?(:clj  (q.debits/index-ids {::m.transactions/id transaction-id
                                                :positive?          true})
                     :cljs (do (comment transaction-id) []))
           idents (m.debits/idents ids)]
       {::positive-debits idents}))
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
                  :cljs (do (comment transaction-id) []))
           idents (m.debits/idents ids)]
       {::negative-debits idents}))
   ::report/column-EQL {::negative-debits [::m.debits/id ::m.debits/value]}})

(>def ::debit-count int?)
(defattr debit-count ::debit-count :number
  {ao/pc-input   #{::debits}
   ao/pc-resolve (fn [_ {::keys [debits]}] {::debit-count (count debits)})})

(defattr user ::user :ref
  {ao/cardinality      :one
   ao/pc-input         #{::m.transactions/id}
   ao/target           ::m.users/id
   ao/pc-output        [{::user [::m.users/id]}]
   ao/pc-resolve
   (fn [_env {::m.transactions/keys [id]}]
     (let [user-id (if id #?(:clj (q.users/find-by-transaction id) :cljs nil) nil)
           ident (when user-id (m.users/ident user-id))]
       {::user ident}))
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(def attributes [admin-index debit-count debits
                 positive-debits negative-debits
                 index user])
