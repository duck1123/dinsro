(ns dinsro.ui.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(form/defsc-form TransactionForm [_this _props]
  {fo/id           m.transactions/id
   fo/attributes   []
   fo/route-prefix "transaction"
   fo/title        "Transaction"})

(defattr transaction-account-link ::m.transactions/account :ref
  {ao/cardinality      :one
   ao/identities       #{::m.transactions/id}
   ao/target           ::m.accounts/id
   ::report/column-EQL {::m.transactions/account (comp/get-query u.links/AccountLink)}})

(report/defsc-report TransactionsReport
  [_this _props]
  {ro/columns          [m.transactions/description
                        transaction-account-link
                        m.transactions/date
                        m.transactions/value]
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this TransactionForm))}}
   ro/control-layout   {:action-buttons [::new-transaction]}
   ro/field-formatters
   {::m.transactions/account (fn [_this props] (u.links/ui-account-link props))}
   ro/route            "transactions"
   ro/row-actions      []
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/all-transactions
   ro/title            "Transaction Report"})

(s/def ::form (s/keys))
(s/def ::toggle-button (s/keys))
