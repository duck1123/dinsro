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
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.event-tags :as mu.n.event-tags]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [../../../joins/nostr/relays.cljc]
;; [../../../model/nostr/relays.cljc]
;; [../../../mutations/nostr/event_tags.cljc]
;; [../../../mutations/nostr/relays.cljc]
;; [../../../ui/nostr.cljs]

(def ident-key ::m.n.event-tags/id)
(def router-key :dinsro.ui.nostr.event-tags/Router)

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
  {ro/column-formatters {::m.n.relays/address      #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/connected
                         m.n.relays/address
                         j.n.relays/connection-count
                         j.n.relays/request-count]
   ro/control-layout    {:action-buttons [::add ::refresh]}
   ro/controls          {::m.n.event-tags/id {:type :uuid :label "id"}
                         ::add               new-item-button
                         ::refresh           u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/subrow-action-button "Fetch Pubkey" ::m.n.event-tags/id ident-key  mu.n.event-tags/fetch!)]
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays"})

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["items"]}
  (log/info :SubPage/starting {:props props})
  ((comp/factory Report) report))
