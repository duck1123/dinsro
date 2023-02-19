(ns dinsro.ui.core.block-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.transactions/tx-id
                        m.c.transactions/fetched?]
   ro/control-layout   {:action-buttons [::fetch ::refresh]}
   ro/controls         {::fetch         (u.links/fetch-button ::m.c.blocks/id mu.c.blocks/fetch-transactions!)
                        ::refresh       u.links/refresh-control
                        ::m.c.blocks/id {:type :uuid :label "Block"}}
   ro/field-formatters {::m.c.transactions/tx-id (u.links/report-link ::m.c.transactions/tx-id u.links/ui-core-tx-link)}
   ro/row-actions      [(u.links/row-action-button "Fetch" ::m.c.transactions/id mu.c.transactions/fetch!)]
   ro/row-pk           m.c.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.transactions/index
   ro/title            "Transactions"}
  (log/finer :Report/starting {:props props})
  (if override-form
    (report/render-layout this)
    (dom/div :.ui.segment
      (report/render-layout this))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {::m.c.blocks/id nil
                       :ui/report      {}}
   :query             [::m.c.blocks/id
                       {:ui/report (comp/get-query Report)}]}
  (log/finer :SubPage/creating {:props props})
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
