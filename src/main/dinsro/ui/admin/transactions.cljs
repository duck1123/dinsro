(ns dinsro.ui.admin.transactions
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

(form/defsc-form NewForm [_this _props]
  {
   fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component u.links/AccountLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/id            m.transactions/id
   fo/route-prefix  "transaction-form"
   fo/title         "Transaction"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date
                        j.transactions/user]
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewForm))}
                        ::refresh         u.links/refresh-control}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "transactions"
   ro/row-actions      []
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.transactions/admin-index
   ro/title            "Admin Transaction Report"})
