(ns dinsro.ui.nostr.events.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]))

;; [../../../actions/nostr/witnesses.clj]

(def ident-key ::m.n.events/id)
(def router-key :dinsro.ui.nostr.events/Router)

(def log-item-props false)

(defsc RunDisplay
  [_this _props]
  {})

(defsc BodyItem
  [_this {::j.n.witnesses/keys [relay] :as props}]
  {:ident         ::m.n.witnesses/id
   :initial-state {::m.n.witnesses/id    nil
                   ::j.n.witnesses/relay {}}
   :query         [::m.n.witnesses/id
                   {::j.n.witnesses/relay (comp/get-query u.links/RelayLinkForm)}]}
  (dom/div {}
    (u.links/ui-relay-link relay)
    (when log-item-props (u.links/log-props props))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.n.witnesses/id}))

(report/defsc-report Report
  [_this props]
  {ro/column-formatters {::m.n.witnesses/event #(u.links/ui-event-link %2)
                         ::m.n.witnesses/run   #(u.links/ui-run-link %2)
                         ::j.n.witnesses/relay #(u.links/ui-relay-link %2)}
   ro/columns           [j.n.witnesses/relay
                         m.n.witnesses/id
                         m.n.witnesses/run]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.events/id {:type :uuid :label "id"}
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/index
   ro/title             "Witnesses"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div {}
      (map ui-body-item current-rows))))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["witnesses"]}
  ((comp/factory Report) report))
