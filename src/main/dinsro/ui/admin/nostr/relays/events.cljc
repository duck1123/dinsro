(ns dinsro.ui.admin.nostr.relays.events
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
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/nostr/events.cljc]]
;; [[../../../../model/nostr/events.cljc]]
;; [[../../../../mutations/nostr/events.cljc]]
;; [[../../../../ui/admin/nostr/events.cljc]]

(def index-page-id :admin-nostr-relays-show-events)
(def model-key ::m.n.pubkeys/id)
(def parent-model-key ::m.n.relays/id)
(def parent-router-id :admin-nostr-relays-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.events/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.events/pubkey  #(when %2 (u.links/ui-admin-pubkey-link %2))
                         ::m.n.events/note-id #(u.links/ui-admin-event-link %3)
                         ::m.n.pubkeys/hex    #(u.links/ui-admin-pubkey-link %3)}
   ro/columns           [m.n.events/content
                         m.n.events/pubkey]
   ro/control-layout    {:action-buttons [::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/admin-index
   ro/title             "Events"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["events"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Events"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
