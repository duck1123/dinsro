(ns dinsro.ui.admin.nostr.relays.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.connections :as mu.n.connections]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

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
   ro/row-actions       [(u.links/row-action-button "Disconnect" ::m.n.connections/id mu.n.connections/disconnect!)]

   ro/row-pk           m.n.connections/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.connections/index
   ro/title            "Connections"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["connections"]}
  ((comp/factory Report) report))