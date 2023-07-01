(ns dinsro.ui.admin.core.blocks.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]))

;; [[../../../../joins/core/transactions.cljc]]
;; [[../../../../model/core/transactions.cljc]]

(def model-key ::m.c.transactions/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/tx-id (u.links/report-link ::m.c.transactions/tx-id u.links/ui-core-tx-link)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?]
   ro/control-layout    {:action-buttons [::fetch ::refresh]}
   ro/controls          {::fetch         (u.buttons/fetch-button ::m.c.blocks/id mu.c.blocks/fetch-transactions!)
                         ::refresh       u.links/refresh-control
                         ::m.c.blocks/id {:type :uuid :label "Block"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" ::m.c.transactions/id mu.c.transactions/fetch!)
                         (u.buttons/row-action-button "Delete" ::m.c.transactions/id mu.c.transactions/delete!)]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [{:ui/report (comp/get-query Report)}]}
  (ui-report report))
