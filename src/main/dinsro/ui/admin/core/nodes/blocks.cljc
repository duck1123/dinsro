(ns dinsro.ui.admin.core.nodes.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/blocks.cljc]]
;; [[../../../../model/core/blocks.cljc]]

(def index-page-key :admin-core-nodes-show-blocks)
(def model-key ::m.c.blocks/id)
(def parent-model-key ::m.c.nodes/id)
(def router-key :dinsro.ui.core.nodes/Router)

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
   ro/controls          {::refresh      u.links/refresh-control
                         ::generate     generate-button
                         ::m.c.nodes/id {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" model-key mu.c.blocks/fetch!)
                         (u.buttons/row-action-button "Delete" model-key mu.c.blocks/delete!)]
   ro/row-pk            m.c.blocks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.blocks/index
   ro/title             "Node Blocks"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.c.nodes/keys [id]
          :ui/keys         [report]
          :as              props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.nodes/id  nil
                       ::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.c.nodes/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(m.navlinks/defroute :admin-core-nodes-show-blocks
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Blocks"
   ::m.navlinks/input-key     ::m.c.nodes/id
   ::m.navlinks/model-key     ::m.c.nodes/id
   ::m.navlinks/parent-key    :admin-core-nodes-show
   ::m.navlinks/router        :admin-core
   ::m.navlinks/required-role :admin})
