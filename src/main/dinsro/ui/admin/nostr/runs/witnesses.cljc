(ns dinsro.ui.admin.nostr.runs.witnesses
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
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../ui/admin/nostr/witnesses.cljc]]

(def index-page-key :admin.nostr-runs-show-witnesses)
(def model-key ::m.n.witnesses/id)
(def parent-model-key ::m.n.runs/id)
(def router-key :dinsro.ui.admin.nostr.runs/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.witnesses/event #(when %2 (u.links/ui-admin-event-link %2))
                         ::m.n.witnesses/relay #(when %2 (u.links/ui-admin-run-link %2))}
   ro/columns           [m.n.witnesses/id
                         m.n.witnesses/event
                         m.n.witnesses/relay]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh     u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/admin-index
   ro/title             "Witnesses"})

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
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Witnesses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr-runs-show
   ::m.navlinks/router        :admin-nostr-runs
   ::m.navlinks/required-role :admin-user})
