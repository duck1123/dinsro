(ns dinsro.ui.core.network-blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/height
                        m.c.blocks/hash
                        m.c.blocks/fetched?]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:inputs [[::m.c.networks/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.blocks/height #(u.links/ui-block-height-link %3)}
   ro/source-attribute ::m.c.blocks/index
   ro/title            "Network Blocks"
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys   [report]
          :as        props
          network-id ::m.c.networks/id}]
  {:query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (dom/div :.ui.segment
    (if network-id
      (ui-report report)
      (dom/p {} "Node ID not set"))))

(def ui-sub-page (comp/factory SubPage))
