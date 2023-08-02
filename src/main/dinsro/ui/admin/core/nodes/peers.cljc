(ns dinsro.ui.admin.core.nodes.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.peers :as j.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/peers.cljc]]
;; [[../../../../model/core/peers.cljc]]

(def index-page-key :admin-core-nodes-show-peers)
(def model-key ::m.c.peers/id)
(def parent-model-key ::m.c.nodes/id)
(def router-key :dinsro.ui.admin.core.nodes/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.peers/delete!))

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.buttons/report-action model-key mu.c.nodes/fetch!)})

(def new-button
  {:type   :button
   :label  "New"
   :action (fn [this]
             (let [props                 (comp/props this)
                   {:ui/keys [controls]} props
                   id-control            (some
                                          (fn [c]
                                            (let [{::control/keys [id]} c]
                                              (when (= id ::m.c.nodes/id)
                                                c)))

                                          controls)
                   node-id (::control/value id-control)]
               (log/info :peers/creating {:props      props
                                          :controls   controls
                                          :id-control id-control
                                          :node-id    node-id})
               (form/create! this u.c.peers/NewForm
                             {:initial-state {::m.c.peers/addr "foo"}})))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.peers/block #(u.links/ui-admin-block-link %2)
                         ::m.c.peers/node  #(u.links/ui-admin-core-node-link %2)}
   ro/columns           [m.c.peers/peer-id
                         m.c.peers/addr
                         m.c.peers/subver
                         m.c.peers/connection-type
                         m.c.peers/node]
   ro/control-layout    {:action-buttons [::new ::fetch ::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {parent-model-key {:type :uuid :label "Nodes"}
                         ::refresh        u.links/refresh-control
                         ::fetch          fetch-button
                         ::new            new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.peers/index
   ro/title             "Node Peers"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report {})}])
   :route-segment     ["peers"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin nodes show peers report"))
    (u.debug/load-error props "admin nodes show peers")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Peers"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-core-nodes-show
   ::m.navlinks/router        :admin-core
   ::m.navlinks/required-role :admin})
