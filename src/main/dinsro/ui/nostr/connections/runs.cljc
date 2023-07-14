(ns dinsro.ui.nostr.connections.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations.nostr.runs :as mu.n.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/nostr/runs.cljc]]
;; [[../../../model/nostr/runs.cljc]]

(def ident-key ::m.n.connections/id)
(def index-page-key :nostr-connections-show-runs)
(def parent-model-key ::m.n.connections/id)
(def router-key :dinsro.ui.nostr.connections/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-request-link %2)}
   ro/columns           [m.n.runs/id
                         m.n.runs/request
                         m.n.runs/connection
                         m.n.runs/status
                         m.n.runs/start-time
                         m.n.runs/end-time]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.connections/id {:type :uuid :label "id"}
                         ::refresh            u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Stop" ::m.n.runs/id mu.n.runs/stop!)
                         (u.buttons/row-action-button "Delete" ::m.n.runs/id mu.n.runs/delete!)]
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/index
   ro/title             "Runs"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["runs"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))

(m.navlinks/defroute   :nostr-connections-show-runs
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Runs"
   ::m.navlinks/model-key     ::m.n.runs/id
   ::m.navlinks/parent-key    :nostr-connections-show
   ::m.navlinks/router        :nostr-connections
   ::m.navlinks/required-role :user})
