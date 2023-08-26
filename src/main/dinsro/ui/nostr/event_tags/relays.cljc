(ns dinsro.ui.nostr.event-tags.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.event-tags :as mu.n.event-tags]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/nostr/relays.cljc]]
;; [[../../../model/nostr/relays.cljc]]
;; [[../../../mutations/nostr/event_tags.cljc]]
;; [[../../../mutations/nostr/relays.cljc]]
;; [[../../../ui/nostr.cljs]]
;; [[../../../ui/nostr/relays.cljc]]

(def index-page-id :nostr-event-tags-show-relays)
(def model-key ::m.n.relays/id)
(def parent-model-key ::m.n.event-tags/id)
(def parent-router-id :nostr-event-tags-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.event-tags/Router)

(def fetch-pubkey-action
  (u.buttons/subrow-action-button "Fetch Pubkey" ::m.n.event-tags/id parent-model-key  mu.n.event-tags/fetch!))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.n.relays/id]
   fo/cancel-route  ["relays"]
   fo/id            m.n.relays/id
   fo/route-prefix  "create-relay"
   fo/title         "Relay"})

(def new-item-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/address
                         j.n.relays/active-connection-count
                         j.n.relays/connection-count
                         j.n.relays/request-count]
   ro/control-layout    {:action-buttons [::add ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::add            new-item-button
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-pubkey-action]
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays"})

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
   :route-segment     ["items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Relays"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
