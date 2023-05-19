(ns dinsro.ui.admin.nostr.relays.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.witnesses/id]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.n.relays/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.witnesses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.witnesses/index
   ro/title            "Witnesses"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["witnesses"]}
  ((comp/factory Report) report))
