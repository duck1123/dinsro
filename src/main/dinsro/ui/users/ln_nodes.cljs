(ns dinsro.ui.users.ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

;; [[../actions/users.clj][User Actions]]

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.nodes/name m.c.chains/name]
   ro/control-layout   {:inputs         [[::m.users/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)}
   ro/row-pk           m.ln.nodes/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.nodes/index
   ro/title            "User Ln Nodes"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["ln-nodes"]}
  ((comp/factory Report) report))
