(ns dinsro.ui.nostr.relays.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def ident-key ::m.n.relays/id)
(def index-page-key :nostr-relays-show-events)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                         ::m.n.events/note-id #(u.links/ui-event-link %3)
                         ::m.n.pubkeys/hex    #(u.links/ui-pubkey-link %3)}
   ro/columns           [m.n.events/content]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.relays/id {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/index
   ro/title             "Events"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id index-page-key
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["events"]}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute   :nostr-relays-show-events
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Events"
   ::m.navlinks/model-key     ::m.n.events/id
   ::m.navlinks/parent-key    :nostr-relays-show
   ::m.navlinks/router        :nostr-relays
   ::m.navlinks/required-role :user})
