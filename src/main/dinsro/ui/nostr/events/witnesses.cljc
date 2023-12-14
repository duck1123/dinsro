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
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.events :as o.n.events]
   [dinsro.options.nostr.witnesses :as o.n.witnesses]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../actions/nostr/witnesses.clj]]
;; [[../../../ui/nostr/witnesses.cljc]]

(def index-page-id :nostr-events-show-witnesses)
(def model-key o.n.witnesses/id)
(def parent-model-key o.n.events/id)
(def parent-router-id :nostr-events-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.events/Router)

(def log-item-props false)

(defsc RunDisplay
  [_this _props]
  {})

(defsc BodyItem
  [_this {::j.n.witnesses/keys [relay] :as props}]
  {:ident         ::m.n.witnesses/id
   :initial-state (fn [_props]
                    {o.n.witnesses/id    nil
                     ::j.n.witnesses/relay (comp/get-initial-state u.links/RelayLinkForm {})})
   :query         (fn []
                    [o.n.witnesses/id
                     {::j.n.witnesses/relay (comp/get-query u.links/RelayLinkForm)}])}
  (dom/div {}
    (u.links/ui-relay-link relay)
    (when log-item-props
      (u.debug/ui-props-logger props))))

(def ui-body-item (comp/factory BodyItem {:keyfn o.n.witnesses/id}))

(report/defsc-report Report
  [_this props]
  {ro/column-formatters {o.n.witnesses/event #(when %2 (u.links/ui-event-link %2))
                         o.n.witnesses/relay #(when %2 (u.links/ui-relay-link %2))}
   ro/columns           [m.n.witnesses/relay
                         m.n.witnesses/id]
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
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Witnesses"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
