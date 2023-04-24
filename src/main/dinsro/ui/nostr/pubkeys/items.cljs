(ns dinsro.ui.nostr.pubkeys.items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/filter  #(u.links/ui-filter-link %2)
                         ::m.n.filter-items/event   #(u.links/ui-event-link %2)
                         ::m.n.filter-items/pubkey  #(u.links/ui-pubkey-link %2)
                         ::j.n.filter-items/request #(u.links/ui-request-link %2)
                         ::j.n.filter-items/relay   #(u.links/ui-relay-link %2)}
   ro/columns           [j.n.filter-items/request
                         m.n.filter-items/filter
                         m.n.filter-items/type
                         m.n.filter-items/index
                         m.n.filter-items/kind
                         m.n.filter-items/event
                         m.n.filter-items/pubkey
                         j.n.filter-items/relay]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.pubkeys/id {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/index
   ro/title             "Filter Items"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["items"]}
  ((comp/factory Report) report))
