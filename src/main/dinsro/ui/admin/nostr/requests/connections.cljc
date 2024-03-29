(ns dinsro.ui.admin.nostr.requests.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.connections :as mu.n.connections]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/nostr/connections.cljc]]
;; [[../../../../model/nostr/connections.cljc]]

(def index-page-id :admin-nostr-requests-show-connections)
(def model-key ::m.n.connections/id)
(def parent-model-key ::m.n.requests/id)
(def parent-router-id :admin-nostr-requests-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.nostr.requests/Router)

(def disconnect-action
  (u.buttons/row-action-button "Disconnect" model-key mu.n.connections/disconnect!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/status    #(u.links/ui-admin-connection-link %3)
                         ::m.n.connections/relay     #(u.links/ui-admin-relay-link %2)
                         ::j.n.connections/run-count #(u.links/ui-admin-connection-run-count-link %3)}
   ro/columns           [m.n.connections/status
                         m.n.connections/start-time
                         m.n.connections/end-time
                         m.n.connections/relay
                         j.n.connections/run-count]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::add-filter     (u.buttons/sub-page-action-button
                                           {:label      "Connect"
                                            :mutation   mu.n.connections/connect!
                                            :parent-key parent-model-key})
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [disconnect-action]
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/admin-index
   ro/title             "Connections"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {::m.navlinks/id  index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         ::m.navlinks/id
                         parent-model-key
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["connections"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Connections"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
