(ns dinsro.ui.reports.admin.accounts.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.reports.transactions :as u.r.transactions]))

(def parent-model-key o.accounts/id)

(report/defsc-report Report
  [_this props]
  {ro/BodyItem          u.r.transactions/BodyItem
   ro/column-formatters {o.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.transactions/description
                         j.transactions/debit-count
                         m.transactions/date]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/admin-index
   ro/title             "Transactions"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div :.ui.items
      (map u.r.transactions/ui-body-item current-rows))))

(def ui-report (comp/factory Report))
