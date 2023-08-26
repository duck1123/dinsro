(ns dinsro.ui.admin.nostr.pubkeys.items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-id :admin-nostr-pubkeys-show-items)
(def model-key ::m.n.filter-items/id)
(def parent-model-key ::m.n.pubkeys/id)
(def parent-router-id :admin-nostr-pubkeys-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/filter  #(when %2 (u.links/ui-admin-filter-link %2))
                         ::m.n.filter-items/event   #(when %2 (u.links/ui-admin-event-link %2))
                         ::m.n.filter-items/pubkey  #(when %2 (u.links/ui-admin-pubkey-link %2))
                         ::j.n.filter-items/request #(when %2 (u.links/ui-admin-request-link %2))
                         ::j.n.filter-items/relay   #(when %2 (u.links/ui-admin-relay-link %2))}
   ro/columns           [j.n.filter-items/request
                         m.n.filter-items/filter
                         m.n.filter-items/type
                         m.n.filter-items/index
                         m.n.filter-items/kind
                         m.n.filter-items/event
                         m.n.filter-items/pubkey
                         j.n.filter-items/relay]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/admin-index
   ro/title             "Filter Items"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
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
   :route-segment     ["items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Filter Items"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
