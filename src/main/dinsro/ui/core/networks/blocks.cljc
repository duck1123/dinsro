(ns dinsro.ui.core.networks.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/blocks.cljc]]
;; [[../../../model/core/blocks.cljc]]

(def index-page-key :core-networks-show-blocks)
(def model-key ::m.c.blocks/id)
(def parent-model-key ::m.c.networks/id)
(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.blocks/height #(u.links/ui-block-height-link %3)}
   ro/columns           [m.c.blocks/height
                         m.c.blocks/hash
                         m.c.blocks/fetched?]
   ro/control-layout    {:inputs         [[::m.c.networks/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::refresh         u.links/refresh-control
                         ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" ::m.c.blocks/id mu.c.blocks/fetch!)
                         (u.buttons/row-action-button "Delete" ::m.c.blocks/id mu.c.blocks/delete!)]
   ro/row-pk            m.c.blocks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.blocks/index
   ro/title             "Blocks"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (if report
    (ui-report report)
    (u.debug/load-error props "networks blocks page")))

(m.navlinks/defroute   :core-networks-show-blocks
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Blocks"
   ::m.navlinks/model-key     ::m.c.blocks/id
   ::m.navlinks/parent-key    :core-networks-show
   ::m.navlinks/router        :core-networks
   ::m.navlinks/required-role :user})
