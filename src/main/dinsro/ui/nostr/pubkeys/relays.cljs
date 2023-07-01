(ns dinsro.ui.nostr.pubkeys.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../model/nostr/relays.cljc][Relays Model]]

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address          #(u.links/ui-relay-link %3)
                         ::j.n.relays/connection-count #(u.links/ui-relay-connection-count-link %3)}
   ro/columns           [m.n.relays/address
                         j.n.relays/connection-count]
   ro/controls          {::m.n.pubkeys/id {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/control-layout    {:action-buttons [::refresh]}
   ro/row-actions       [(u.buttons/subrow-action-button "Fetch" ::m.n.relays/id ident-key  mu.n.pubkeys/fetch!)
                         (u.buttons/subrow-action-button "Fetch Events" ::m.n.relays/id ident-key mu.n.events/fetch-events!)]
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
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :route-segment     ["relays"]}
  (ui-report report))
