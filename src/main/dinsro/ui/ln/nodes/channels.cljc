(ns dinsro.ui.ln.nodes.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
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
   [dinsro.ui.forms.ln.channels :as u.f.ln.channels]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/channels.cljc]]
;; [[../../../model/ln/channels.cljc]]

(def index-page-id :ln-nodes-show-channels)
(def model-key ::m.ln.channels/id)
(def parent-model-key ::m.ln.nodes/id)
(def parent-router-id :ln-nodes-show)
(def required-role :user)
(def router-key :dinsro.ui.ln.nodes/Router)

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
               (form/create! this u.f.ln.channels/NewForm
                             {:initial-state {::m.ln.channels/address "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.channels/block #(u.links/ui-block-link %2)
                         ::m.ln.channels/node  #(u.links/ui-core-node-link %2)}
   ro/columns           [m.ln.channels/node]
   ro/control-layout    {:action-buttons [::new ::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
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
   ro/source-attribute  ::j.ln.channels/index
   ro/title             "Node Channels"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {::m.navlinks/id  index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["channels"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Channels"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
