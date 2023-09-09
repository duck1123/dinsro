(ns dinsro.ui.admin.ln.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.ln.nodes :as o.ln.nodes]
   [dinsro.options.ln.peers :as o.ln.peers]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.forms.admin.ln.nodes.peers :as u.f.a.ln.n.peers]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../ui/admin/ln/nodes.cljc]]

(def index-page-id :admin-ln-nodes-show-peers)
(def model-key o.ln.peers/id)
(def parent-model-key o.ln.nodes/id)
(def parent-router-id :admin-ln-nodes-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.ln.nodes/Router)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.ln.n.peers/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.peers/node
                        m.ln.peers/remote-node
                        m.ln.peers/inbound?]
   ro/controls         {::new new-button}
   ro/field-formatters {o.ln.peers/node        #(u.links/ui-admin-ln-node-link %2)
                        o.ln.peers/remote-node #(u.links/ui-admin-remote-node-link %2)}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.ln.peers/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.peers/index
   ro/title            "Lightning Peers"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {o.navlinks/id    index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         o.navlinks/id
                         parent-model-key
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Peers"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
