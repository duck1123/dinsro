(ns dinsro.ui.nostr.requests.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filter-items :as mu.n.filter-items]
   [dinsro.ui.links :as u.links]))

;; [../filters/filter_items.cljs]

(def ident-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/filter #(u.links/ui-filter-link %2)
                         ::m.n.filter-items/pubkey #(u.links/ui-pubkey-link %2)}
   ro/columns           [m.n.filter-items/id
                         m.n.filter-items/filter
                         m.n.filter-items/type
                         m.n.filter-items/kind
                         m.n.filter-items/event
                         m.n.filter-items/pubkey]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.requests/id {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
   ro/row-actions       [(u.links/row-action-button "Delete" ::m.n.filter-items/id mu.n.filter-items/delete!)]
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
   :route-segment     ["filter-items"]}
  ((comp/factory Report) report))
