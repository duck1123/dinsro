(ns dinsro.ui.user-ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name]
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

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params %})
   :initial-state     {::m.users/id nil
                       :ui/report   {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
