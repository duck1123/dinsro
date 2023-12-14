(ns dinsro.ui.admin.core.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.core.nodes :as o.c.nodes]
   [dinsro.options.core.peers :as o.c.peers]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.admin.core.nodes.peers :as u.r.a.c.n.peers]))

;; [[../../../../joins/core/peers.cljc]]
;; [[../../../../model/core/peers.cljc]]

(def index-page-id :admin-core-nodes-show-peers)
(def model-key o.c.peers/id)
(def parent-model-key o.c.nodes/id)
(def parent-router-id :admin-core-nodes-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.nodes/Router)

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % u.r.a.c.n.peers/Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state u.r.a.c.n.peers/Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query u.r.a.c.n.peers/Report {})}])
   :route-segment     ["peers"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props u.r.a.c.n.peers/ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
