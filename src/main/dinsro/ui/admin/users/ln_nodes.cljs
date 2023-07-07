(ns dinsro.ui.admin.users.ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../actions/users.clj]]
;; [[../../../joins/ln/nodes.cljc]]
;; [[../../../model/ln/nodes.cljc]]

(def index-page-key :admin-users-ln-nodes)
(def model-key ::m.ln.nodes/id)
(def parent-model-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)}
   ro/columns           [m.ln.nodes/name m.c.chains/name]
   ro/control-layout    {:inputs         [[::m.users/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "User Ln Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.users/keys [id]
          :ui/keys       [report]
          :as            props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.users/id    nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["ln-nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
