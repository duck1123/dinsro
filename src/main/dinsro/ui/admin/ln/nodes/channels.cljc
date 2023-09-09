(ns dinsro.ui.admin.ln.nodes.channels
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.control :as control]
            [com.fulcrologic.rad.form :as form]
            [com.fulcrologic.rad.report :as report]
            [com.fulcrologic.rad.report-options :as ro]
            [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
            [dinsro.joins.ln.channels :as j.ln.channels]
            [dinsro.model.ln.channels :as m.ln.channels]
            [dinsro.model.ln.nodes :as m.ln.nodes]
            [dinsro.model.navlinks :as m.navlinks]
            [dinsro.mutations.ln.channels :as mu.ln.channels]
            [dinsro.options.navlinks :as o.navlinks]
            [dinsro.ui.buttons :as u.buttons]
            [dinsro.ui.controls :as u.controls]
            [dinsro.ui.forms.admin.ln.nodes.channels :as u.f.a.ln.n.channels]
            [dinsro.ui.links :as u.links]
            [dinsro.ui.loader :as u.loader]
            [lambdaisland.glogc :as log]))

;; [[../../../../ui/admin/ln/nodes.cljc]]

(def index-page-id :admin-ln-nodes-show-channels)
(def model-key ::m.ln.channels/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router-id :admin-ln-nodes-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.ln.nodes/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.ln.channels/delete!))

(def new-button
  {:type   :button
   :label  "New"
   :action (fn [this]
             (let [props                 (comp/props this)
                   {:ui/keys [controls]} props
                   id-control            (some
                                          (fn [c]
                                            (let [{::control/keys [id]} c]
                                              (when (= id ::m.ln.nodes/id)
                                                c)))

                                          controls)
                   node-id (::control/value id-control)]
               (log/info :channels/creating {:props      props
                                             :controls   controls
                                             :id-control id-control
                                             :node-id    node-id})
               (form/create! this u.f.a.ln.n.channels/NewForm
                             {:initial-state {::m.ln.channels/address "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.channels/block #(u.links/ui-admin-block-link %2)
                         ::m.ln.channels/node  #(u.links/ui-admin-core-node-link %2)}
   ro/columns           [m.ln.channels/node]
   ro/control-layout    {:action-buttons [::new ::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh       u.links/refresh-control
                         ::new           new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "node-channels"
   ro/row-actions       [delete-action]
   ro/row-pk            m.ln.channels/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.channels/admin-index
   ro/title             "Node Channels"})

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
   o.navlinks/label         "Channels"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
