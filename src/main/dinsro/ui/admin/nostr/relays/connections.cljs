(ns dinsro.ui.admin.nostr.relays.connections
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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/nostr/connections.cljc]]
;; [[../../../../model/nostr/connections.cljc]]

(def index-page-key :admin-nostr-relays-show-connections)
(def model-key ::m.n.connections/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(def disconnect-action
  (u.buttons/row-action-button "Disconnect" model-key mu.n.connections/disconnect!))

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
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [disconnect-action]
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/index
   ro/title             "Connections"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.n.relays/keys [id]
          :ui/keys          [report]
          :as               props}]
  {:componentDidMount
   ;; #(report/start-report! % Report {:route-params (comp/props %)})
   (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.n.relays/id nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.n.relays/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["connections"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin relay connection page")))
