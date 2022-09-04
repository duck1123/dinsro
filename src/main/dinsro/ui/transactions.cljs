(ns dinsro.ui.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

(defsc AccountQuery
  [_this _props]
  {:query [::m.accounts/id ::m.accounts/name]
   :ident ::m.accounts/id})

(form/defsc-form TransactionForm [_this _props]
  {fo/id            m.transactions/id
   fo/attributes    [m.transactions/description
                     m.transactions/value
                     j.transactions/user
                     m.transactions/account]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component AccountQuery
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/route-prefix  "transaction"
   fo/subforms      {::m.transactions/account {fo/ui u.links/AccountLinkForm}
                     ::m.transactions/user    {fo/ui u.links/UserLinkForm}}
   fo/title         "Transaction"})

(report/defsc-report TransactionsReport
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/account
                        m.transactions/date
                        m.transactions/value
                        j.transactions/user]
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this TransactionForm))}
                        ::refresh         u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/field-formatters {::m.transactions/account #(u.links/ui-account-link %2)}
   ro/form-links       {::m.transactions/description TransactionForm}
   ro/route            "transactions"
   ro/row-actions      []
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/index
   ro/title            "Transaction Report"})
