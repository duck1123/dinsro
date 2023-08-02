(ns dinsro.ui.admin.nostr.relays.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations.nostr.runs :as mu.n.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/nostr/runs.cljc]]
;; [[../../../../model/nostr/runs.cljc]]

(def index-page-key :admin-nostr-relays-show-runs)
(def model-key ::m.n.runs/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.runs/delete!))

(def stop-action
  (u.buttons/row-action-button "Stop" model-key mu.n.runs/stop!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-admin-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-admin-request-link %2)
                         ::m.n.runs/status     #(u.links/ui-admin-run-link %3)}
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
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["runs"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin relays runs report"))
    (u.debug/load-error props "admin relays runs page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Runs"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr-relays-show
   ::m.navlinks/router        :admin-nostr-relays
   ::m.navlinks/required-role :admin})
