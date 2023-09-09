(ns dinsro.ui.nostr.event-tags.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.event-tags :as mu.n.event-tags]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.event-tags :as o.n.event-tags]
   [dinsro.options.nostr.relays :as o.n.relays]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.forms.nostr.event-tags.relays :as u.f.n.et.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/nostr/relays.cljc]]
;; [[../../../model/nostr/relays.cljc]]
;; [[../../../mutations/nostr/event_tags.cljc]]
;; [[../../../mutations/nostr/relays.cljc]]
;; [[../../../ui/nostr.cljs]]
;; [[../../../ui/nostr/relays.cljc]]

(def index-page-id :nostr-event-tags-show-relays)
(def model-key o.n.relays/id)
(def parent-model-key o.n.event-tags/id)
(def parent-router-id :nostr-event-tags-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.event-tags/Router)

(def fetch-pubkey-action
  (u.buttons/subrow-action-button "Fetch Pubkey" model-key parent-model-key mu.n.event-tags/fetch!))

(def new-item-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this u.f.n.et.relays/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.n.relays/address #(u.links/ui-relay-link %3)}
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
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id    index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["relays"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Relays"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
