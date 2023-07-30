(ns dinsro.ui.nostr.pubkeys.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/relays.cljc]]

(def index-page-key :nostr-pubkeys-show-relays)
(def model-key ::m.n.relays/id)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(def fetch-action
  (u.buttons/subrow-action-button "Fetch" model-key parent-model-key  mu.n.pubkeys/fetch!))

(def fetch-events-action
  (u.buttons/subrow-action-button "Fetch Events" model-key parent-model-key mu.n.events/fetch-events!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address          #(u.links/ui-relay-link %3)
                         ::j.n.relays/connection-count #(u.links/ui-relay-connection-count-link %3)}
   ro/columns           [m.n.relays/address
                         j.n.relays/connection-count]
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/control-layout    {:action-buttons [::refresh]}
   ro/row-actions       [fetch-action
                         fetch-events-action]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       {}})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["relays"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Relays"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-pubkeys-show
   ::m.navlinks/router        :nostr-pubkeys
   ::m.navlinks/required-role :user})
