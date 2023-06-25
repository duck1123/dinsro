(ns dinsro.ui.nostr.events.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [../../../joins/nostr/relays.cljc]
;; [../../../model/nostr/relays.cljc]

(def ident-key ::m.n.events/id)
(def index-page-key :nostr-events-relays)
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

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["relays"]}
  (ui-report report))
