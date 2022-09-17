(ns dinsro.ui.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.transaction-debits :as u.transaction-debits]))

(defsc AccountQuery
  [_this _props]
  {:query [::m.accounts/id ::m.accounts/name]
   :ident ::m.accounts/id})

(form/defsc-form NewTransactionForm [_this _props]
  {fo/id            m.transactions/id
   fo/attributes    [m.transactions/description
                     j.transactions/user]
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
   fo/route-prefix  "transaction-form"
   fo/title         "Transaction"})

(report/defsc-report TransactionsReport
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date]
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewTransactionForm))}
                        ::refresh         u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "transactions"
   ro/row-actions      []
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/index
   ro/title            "Transaction Report"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date
                        j.transactions/user]
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewTransactionForm))}
                        ::refresh         u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "transactions"
   ro/row-actions      []
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/admin-index
   ro/title            "Admin Transaction Report"})

(defsc ShowTransaction
  [_this {::m.transactions/keys [description date]
          :ui/keys              [debits]}]
  {:route-segment ["transactions" :id]
   :query         [::m.transactions/description
                   ::m.transactions/id
                   ::m.transactions/date
                   {:ui/debits (comp/get-query u.transaction-debits/SubPage)}]
   :initial-state {::m.transactions/description ""
                   ::m.transactions/id          nil
                   ::m.transactions/date        ""}
   :ident         ::m.transactions/id
   :pre-merge     (u.links/page-merger
                   ::m.transactions/id
                   {:ui/debits u.transaction-debits/SubPage})
   :will-enter    (partial u.links/page-loader ::m.transactions/id ::ShowTransaction)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Transaction: " (str description))
     (dom/p {} "Date: " (str date)))
   (dom/div  :.ui.segment
     (if debits
       (u.transaction-debits/ui-sub-page debits)
       (dom/p {} "Transaction debits not loaded")))))
