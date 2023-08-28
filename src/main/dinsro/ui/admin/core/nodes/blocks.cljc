(ns dinsro.ui.admin.core.nodes.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/blocks.cljc]]
;; [[../../../../model/core/blocks.cljc]]

(def index-page-id :admin-core-nodes-show-blocks)
(def model-key ::m.c.blocks/id)
(def parent-model-key ::m.c.nodes/id)
(def parent-router-id :admin-core-nodes-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.nodes/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.blocks/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.blocks/fetch!))

(def generate-button
  {:label  "Generate"
   :local? true
   :type   :button
   :action (fn [this _]
             (let [props      (comp/props this)
                   parameters (:ui/parameters props)
                   node-id    (::m.c.nodes/id parameters)]
               (log/info :generate-button/clicked {:props props :node-id node-id})
               (comp/transact! this [`(mu.c.nodes/generate! {::m.c.nodes/id ~node-id})])))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.blocks/hash    #(u.links/ui-admin-block-link %3)
                         ::m.c.blocks/network #(u.links/ui-admin-network-link %2)}
   ro/columns           [m.c.blocks/hash
                         m.c.blocks/height
                         m.c.blocks/fetched?
                         m.c.blocks/network]
   ro/control-layout    {:action-buttons [::generate ::refresh]}
   ro/controls          {::refresh        u.links/refresh-control
                         ::generate       generate-button
                         parent-model-key {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action
                         delete-action]
   ro/row-pk            m.c.blocks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.blocks/index
   ro/title             "Node Blocks"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn [_]
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["blocks"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Blocks"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
