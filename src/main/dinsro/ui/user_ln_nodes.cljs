(ns dinsro.ui.user-ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name m.c.chains/name]
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:inputs [[::m.users/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)}
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.nodes/index
   ro/title            "User Ln Nodes"})

(def ui-report (comp/factory Report))

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["ln-nodes"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))
