(ns dinsro.ui.core.block-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.tx/tx-id
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls         {::fetch         {:type   :button
                                         :label  "Fetch"
                                         :action (u.links/report-action ::m.c.blocks/id mu.c.blocks/fetch-transactions!)}
                        ::refresh       u.links/refresh-control
                        ::m.c.blocks/id {:type :uuid :label "Block"}}
   ro/control-layout   {:action-buttons [::fetch ::refresh]}
   ro/field-formatters {::m.c.tx/block #(u.links/ui-block-height-link %2)
                        ::m.c.tx/tx-id (u.links/report-link ::m.c.tx/tx-id u.links/ui-core-tx-link)}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Transactions"
   ro/row-actions      [u.c.tx/fetch-action-button u.c.tx/delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true}
  (log/finer :Report/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.blocks/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.blocks/id nil
                       :ui/report      {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
