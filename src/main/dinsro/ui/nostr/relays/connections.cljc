(ns dinsro.ui.nostr.relays.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.connections :as mu.n.connections]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.n.relays/id)
(def index-page-key :nostr-relays-show-connections)
(def router-key :dinsro.ui.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/relay     #(u.links/ui-relay-link %2)
                         ::m.n.connections/status    #(u.links/ui-connection-link %3)
                         ::j.n.connections/run-count #(u.links/ui-connection-run-count-link %3)}
   ro/columns           [m.n.connections/status
                         m.n.connections/relay
                         m.n.connections/start-time
                         m.n.connections/end-time
                         j.n.connections/run-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.relays/id {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Disconnect" ::m.n.connections/id mu.n.connections/disconnect!)]

   ro/row-pk           m.n.connections/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.connections/index
   ro/title            "Connections"})

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
   :route-segment     ["connections"]}
  (ui-report report))

(m.navlinks/defroute   :nostr-relays-show-connections
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Connections"
   ::m.navlinks/model-key     ::m.n.connections/id
   ::m.navlinks/parent-key    :nostr-pubkeys-show
   ::m.navlinks/router        :nostr-relays
   ::m.navlinks/required-role :user})
