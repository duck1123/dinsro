(ns dinsro.ui.nostr.events.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [../../../actions/nostr/witnesses.clj]

(def index-page-key :nostr-events-show-witnesses)
(def model-key ::m.n.witnesses/id)
(def parent-model-key ::m.n.events/id)
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
    (when log-item-props
      (u.debug/log-props props))))

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
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
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

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "event show witnesses report"))
    (u.debug/load-error props "event show witnesses page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Witnesses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-event-show
   ::m.navlinks/router        :nostr-events
   ::m.navlinks/required-role :user})
