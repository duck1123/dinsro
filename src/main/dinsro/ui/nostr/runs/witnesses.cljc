(ns dinsro.ui.nostr.runs.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.n.runs/id)
(def index-page-key :nostr-runs-show-witnesses)
(def router-key :dinsro.ui.nostr.runs/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.witnesses/event #(u.links/ui-event-link %2)
                         ::m.n.witnesses/run   #(u.links/ui-run-link %2)}
   ro/columns           [m.n.witnesses/id
                         m.n.witnesses/event
                         m.n.witnesses/run]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.runs/id {:type :uuid :label "id"}
                         ::refresh     u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/index
   ro/title             "Witnesses"})

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
   :route-segment     ["witnesses"]}
  (ui-report report))

(m.navlinks/defroute   :nostr-runs-show-witnesses
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Witnesses"
   ::m.navlinks/model-key     ::m.n.witnesses/id
   ::m.navlinks/parent-key    :nostr-runs-show
   ::m.navlinks/router        :nostr-runs
   ::m.navlinks/required-role :user})
