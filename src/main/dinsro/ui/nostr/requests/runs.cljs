(ns dinsro.ui.nostr.requests.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations.nostr.runs :as mu.n.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.n.requests/id)
(def index-page-key :nostr-requests-show-runs)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/status     #(u.links/ui-run-link %3)
                         ::m.n.runs/connection #(u.links/ui-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-request-link %2)}
   ro/columns           [m.n.runs/status
                         m.n.runs/request
                         m.n.runs/connection
                         m.n.runs/start-time
                         m.n.runs/end-time]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.requests/id {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
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
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["runs"]}
  (ui-report report))
