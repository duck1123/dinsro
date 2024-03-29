(ns dinsro.ui.nostr.relays
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.nostr.relays :as u.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.relays.connections :as u.n.r.connections]
   [dinsro.ui.nostr.relays.events :as u.n.r.events]
   [dinsro.ui.nostr.relays.pubkeys :as u.n.r.pubkeys]
   [dinsro.ui.nostr.relays.requests :as u.n.r.requests]
   [dinsro.ui.nostr.relays.runs :as u.n.r.runs]
   [dinsro.ui.nostr.relays.witnesses :as u.n.r.witnesses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../mutations/nostr/relays.cljc]]
;; [[../../queries/nostr/relays.clj]]
;; [[../../ui/admin/nostr/relays.cljc]]
;; [[../../ui/nostr/event_tags/relays.cljc]]

(def index-page-id :nostr-relays)
(def model-key ::m.n.relays/id)
(def parent-router-id :nostr)
(def required-role :user)
(def show-page-id :nostr-relays-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.relays/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New Relay"
   :action (fn [this _] (form/create! this u.n.relays/NewRelayForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::j.n.relays/connection-count #(u.links/ui-admin-relay-connection-count-link %3)
                         ::j.n.relays/request-count    #(u.links/ui-relay-request-count-link %3)
                         ::m.n.relays/address          #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/address
                         j.n.relays/request-count
                         j.n.relays/connection-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/row-actions       [delete-action]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.connections/SubPage
    u.n.r.pubkeys/SubPage
    u.n.r.requests/SubPage
    u.n.r.runs/SubPage
    u.n.r.events/SubPage
    u.n.r.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.n.r.connections/index-page-id
    u.n.r.pubkeys/index-page-id
    u.n.r.events/index-page-id
    u.n.r.pubkeys/index-page-id
    u.n.r.runs/index-page-id
    u.n.r.witnesses/index-page-id]})

(defsc Show
  [_this {::m.n.relays/keys [address id]
          ::j.n.relays/keys [connection-count]
          :ui/keys          [nav-menu router]
          :as               props}]
  {:ident         ::m.n.relays/id
   :initial-state (fn [{::m.n.relays/keys [id] :as props}]
                    (log/trace :Show/starting {:props props})
                    {::m.n.relays/id               nil
                     ::m.n.relays/address          ""
                     ::j.n.relays/connection-count 0
                     :ui/nav-menu                  (comp/get-initial-state u.menus/NavMenu
                                                     {::m.navbars/id show-page-id
                                                      :id            id})
                     :ui/router                    (comp/get-initial-state Router)})
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   ::j.n.relays/connection-count
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/dl {}
            (dom/dt {} "Address")
            (dom/dd {} (str address))
            (dom/dt {} "Connections")
            (dom/dd {} (str connection-count))))
        (if nav-menu
          (u.menus/ui-nav-menu nav-menu)
          (u.debug/load-error props "relay show menu"))
        (ui-router router)))
    (u.debug/load-error props "show relay")))

(def ui-show (comp/factory Show))

(defsc RelayDisplay
  [_this props]
  {:ident         ::m.n.relays/id
   :initial-state {::m.n.relays/id     nil
                   ::m.n.relays/address ""}
   :query         [::m.n.relays/id
                   ::m.n.relays/address]}
  (u.links/ui-admin-relay-link props))

(def ui-relay-display (comp/factory RelayDisplay {:keyfn ::m.n.relays/id}))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["relays"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["relay" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Relay"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
