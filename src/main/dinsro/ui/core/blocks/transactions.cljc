(ns dinsro.ui.core.blocks.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]))

;; [[../../../joins/core/transactions.cljc]]
;; [[../../../model/core/transactions.cljc]]

(def index-page-id :core-blocks-show-transactions)
(def model-key ::m.c.transactions/id)
(def parent-model-key ::m.c.blocks/id)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/tx-id (u.links/report-link ::m.c.transactions/tx-id u.links/ui-core-tx-link)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?]
   ro/control-layout    {:action-buttons [::fetch ::refresh]}
   ro/controls          {::fetch          (u.buttons/fetch-button parent-model-key mu.c.blocks/fetch-transactions!)
                         ::refresh        u.links/refresh-control
                         parent-model-key {:type :uuid :label "id"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action delete-action]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

(defsc SubSection
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(def ui-sub-section (comp/factory SubSection))
