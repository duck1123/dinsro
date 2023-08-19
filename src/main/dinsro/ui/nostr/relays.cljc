(ns dinsro.ui.nostr.relays
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
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

(def index-page-key :nostr-relays)
(def model-key ::m.n.relays/id)
(def show-menu-id :nostr-relays)
(def show-page-key :nostr-relays-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.relays/delete!))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props (comp/props this)
                   address (::m.n.relays/address props)]
               (log/info :submit-button/clicked {:address address})
               (comp/transact! this
                 [`(mu.n.relays/submit! {::m.n.relays/address ~address})])))})

(form/defsc-form NewRelayForm [_this _props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.n.relays/address]
   fo/cancel-route   ["relays"]
   fo/controls       {::submit submit-button}
   fo/id             m.n.relays/id
   fo/route-prefix   "new-relay"
   fo/title          "Relay"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Relay"
   :action (fn [this _] (form/create! this NewRelayForm))})

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

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :nostr
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.n.r.connections/index-page-key
    u.n.r.pubkeys/index-page-key
    u.n.r.events/index-page-key
    u.n.r.pubkeys/index-page-key
    u.n.r.runs/index-page-key
    u.n.r.witnesses/index-page-key]})

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
                                                     {::m.navbars/id show-menu-id
                                                      :id            id})
                     :ui/router                    (comp/get-initial-state Router)})
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
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
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["relays"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["relay" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (get props model-key)
    (if target
      (ui-show target)
      (u.debug/load-error props "show relay target"))
    (u.debug/load-error props "show relay")))

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Relay"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr
   ::m.navlinks/router        :nostr
   ::m.navlinks/required-role :user})
