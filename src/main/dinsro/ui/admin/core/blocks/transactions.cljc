(ns dinsro.ui.admin.core.blocks.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
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
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/core/transactions.cljc]]
;; [[../../../../model/core/transactions.cljc]]

(def index-page-id :admin-core-blocks-show-transactions)
(def model-key ::m.c.transactions/id)
(def parent-model-key ::m.c.blocks/id)
(def parent-router-id :admin-core-blocks-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.blocks/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/tx-id
                         (u.links/report-link ::m.c.transactions/tx-id u.links/ui-core-tx-link)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?]
   ro/control-layout    {:action-buttons [::fetch ::refresh]}
   ro/controls          {::fetch          (u.buttons/fetch-button parent-model-key mu.c.blocks/fetch-transactions!)
                         ::refresh        u.links/refresh-control
                         parent-model-key {:type :uuid :label "Block"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action delete-action]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/admin-index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Transactions"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
