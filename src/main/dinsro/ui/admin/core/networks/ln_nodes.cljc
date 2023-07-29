(ns dinsro.ui.admin.core.networks.ln-nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/ln/nodes.cljc]]

(def index-page-key :admin-core-networks-show-ln-nodes)
(def model-key ::m.ln.nodes/id)
(def parent-model-key ::m.c.networks/id)
(def router-key :dinsro.ui.admin.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.nodes/name #(u.links/ui-node-link %3)
                         ::m.ln.nodes/user #(u.links/ui-user-link %2)}
   ro/columns           [m.ln.nodes/name
                         m.ln.nodes/user]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh        u.links/refresh-control
                         parent-model-key {:type :uuid :label "Network"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "Lightning Nodes"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_]
                        {parent-model-key nil
                         ::m.navlinks/id  index-page-key
                         :ui/report       {}})
   :query             (fn [_]
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["ln-nodes"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :ShowPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin networks show ln nodes"))
    (u.debug/load-error props "admin networks show ln nodes")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "LN Nodes"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-core-networks-show
   ::m.navlinks/router        :admin-core-networks
   ::m.navlinks/required-role :admin})
