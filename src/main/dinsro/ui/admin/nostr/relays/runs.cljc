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

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-request-link %2)
                         ::m.n.runs/status     #(u.links/ui-run-link %3)}
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
   ro/row-actions       [(u.buttons/row-action-button "Stop" model-key mu.n.runs/stop!)
                         (u.buttons/row-action-button "Delete" model-key mu.n.runs/delete!)]
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/index
   ro/title             "Runs"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.n.relays/keys [id]
          :ui/keys          [report]
          :as               props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.n.relays/id nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.n.relays/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["runs"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin relays runs")))

(m.navlinks/defroute
  :admin-nostr-relays-show-runs
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     ::m.n.relays/id
   ::m.navlinks/label         "Runs"
   ::m.navlinks/model-key     ::m.n.runs/id
   ::m.navlinks/parent-key    :admin-nostr-relays-show
   ::m.navlinks/router        :admin-nostr-relays
   ::m.navlinks/required-role :admin})
