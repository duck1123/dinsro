(ns dinsro.ui.nostr.requests.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filters :as mu.n.filters]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/index      #(u.links/ui-filter-link %3)
                         ::m.n.filters/request    #(u.links/ui-request-link %2)
                         ::j.n.filters/item-count #(u.links/ui-filter-item-count-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         j.n.filters/item-count]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.requests/id {:type :uuid :label "id"}
                         ::add-filter      (u.links/sub-page-action-button
                                            {:label      "Add Filter"
                                             :mutation   mu.n.filters/add-filter!
                                             :parent-key ident-key})
                         ::refresh         u.links/refresh-control}
   ro/row-actions       [(u.links/row-action-button "Delete" ::m.n.filters/id mu.n.filters/delete!)]
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/index
   ro/title             "Filters"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["filters"]}
  ((comp/factory Report) report))
