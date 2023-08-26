(ns dinsro.ui.admin.nostr.connections.runs
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
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../actions/nostr/runs.clj]]
;; [[../../../../joins/nostr/runs.cljc]]
;; [[../../../../model/nostr/runs.cljc]]
;; [[../../../../mutations/nostr/runs.cljc]]
;; [[../../../../ui/nostr/runs.cljc]]

(def index-page-id :nostr-connections-show-runs)
(def model-key ::m.n.runs/id)
(def parent-model-key ::m.n.connections/id)
(def parent-router-id :nostr-connections-show)
(def required-role :admin)
(def router-key :dinsro.ui.nostr.connections/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.runs/delete!))

(def stop-action
  (u.buttons/row-action-button "Stop" model-key mu.n.runs/stop!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::j.n.runs/relay      #(when %2 (u.links/ui-admin-relay-link %2))
                         ::m.n.runs/connection #(when %2 (u.links/ui-admin-connection-link %2))
                         ::m.n.runs/request    #(when %2 (u.links/ui-admin-request-link %2))
                         ::m.n.runs/status     #(u.links/ui-admin-run-link %3)
                         ::m.n.runs/start-time u.controls/date-formatter
                         ::m.n.runs/end-time   u.controls/date-formatter}
   ro/columns           [m.n.runs/status
                         m.n.runs/request
                         m.n.runs/connection
                         j.n.runs/relay
                         m.n.runs/start-time
                         m.n.runs/end-time]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [stop-action delete-action]
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/admin-index
   ro/title             "Runs"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["runs"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if (parent-model-key props)
    (ui-report report)
    (u.debug/load-error props "admin connections runs")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Runs"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
