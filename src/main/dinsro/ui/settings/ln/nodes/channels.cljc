(ns dinsro.ui.settings.ln.nodes.channels
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
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :settings-ln-nodes-show-channels)
(def model-key ::m.ln.channels/id)
(def parent-model-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

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
               (form/create! this u.ln.channels/NewForm
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
                         ::new new-button}

   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "node-channels"
   ro/row-actions      [(u.buttons/row-action-button "Delete" ::m.ln.channels/id mu.ln.channels/delete!)]
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.channels/index
   ro/title            "Node Channels"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["channels"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Channels"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :settings-ln-nodes-show
   ::m.navlinks/router        :settings-ln-nodes
   ::m.navlinks/required-role :user})
