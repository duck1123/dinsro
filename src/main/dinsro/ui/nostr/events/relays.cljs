(ns dinsro.ui.nostr.events.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.links :as u.links]))

;; [../../../joins/nostr/relays.cljc]
;; [../../../model/nostr/relays.cljc]

(def ident-key ::m.n.events/id)
(def router-key :dinsro.ui.nostr.events/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.n.relays/id
                         m.n.relays/address]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.events/id {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["relays"]}
  ((comp/factory Report) report))