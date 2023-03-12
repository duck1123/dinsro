(ns dinsro.ui.nostr.filters.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.filters/id)
(def router-key :dinsro.ui.nostr.filters/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.filter-items/id]
   ro/controls         {::m.n.filters/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::j.n.filter-items/index
   ro/title            "Filter Items"
   ro/row-pk           m.n.filter-items/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["items"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
