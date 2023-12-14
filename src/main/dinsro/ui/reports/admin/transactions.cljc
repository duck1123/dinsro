(ns dinsro.ui.reports.admin.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin.transactions :as u.f.a.transactions]
   [dinsro.ui.links :as u.links]))

(def model-key o.transactions/id)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.transactions/delete!))

(def new-button
  (u.buttons/form-create-button "New Transaction" u.f.a.transactions/AdminTransactionForm))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-admin-transaction-link %3)}
   ro/columns           [m.transactions/description
                         m.transactions/date
                         j.transactions/debit-count]
   ro/control-layout    {:action-buttons [::new-transaction ::refresh]}
   ro/controls          {::new-transaction new-button
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/admin-index
   ro/title             "Admin Transaction Report"})

(def ui-report (comp/factory Report))
