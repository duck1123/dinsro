(ns dinsro.ui.admin.nostr.relays
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
   [dinsro.ui.admin.nostr.relays.connections :as u.a.n.r.connections]
   [dinsro.ui.admin.nostr.relays.events :as u.a.n.r.events]
   [dinsro.ui.admin.nostr.relays.pubkeys :as u.a.n.r.pubkeys]
   [dinsro.ui.admin.nostr.relays.requests :as u.a.n.r.requests]
   [dinsro.ui.admin.nostr.relays.runs :as u.a.n.r.runs]
   [dinsro.ui.admin.nostr.relays.witnesses :as u.a.n.r.witnesses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/relays.cljc]]
;; [[../../../model/nostr/relays.cljc]]
;; [[../../../ui/admin/nostr/relays/connections.cljc]]
;; [[../../../ui/nostr/relays.cljs]]

(def index-page-key :admin-nostr-relays)
(def model-key ::m.n.relays/id)
(def show-menu-id :admin-nostr-relays)
(def show-page-key :admin-nostr-relays-show)

(def log-props? false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.relays/delete!))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props   (comp/props this)
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
  {ro/column-formatters {::m.n.relays/address          #(u.links/ui-admin-relay-link %3)
                         ::j.n.relays/connection-count #(u.links/ui-admin-relay-connection-count-link %3)
                         ::j.n.relays/request-count    #(u.links/ui-admin-relay-request-count-link %3)}
   ro/columns           [m.n.relays/address
                         m.n.relays/connected
                         j.n.relays/request-count
                         j.n.relays/connection-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "relays"
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.r.connections/SubPage
    u.a.n.r.requests/SubPage
    u.a.n.r.events/SubPage
    u.a.n.r.pubkeys/SubPage
    u.a.n.r.runs/SubPage
    u.a.n.r.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :admin-nostr
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.n.r.connections/index-page-key
    u.a.n.r.requests/index-page-key
    u.a.n.r.events/index-page-key
    u.a.n.r.pubkeys/index-page-key
    u.a.n.r.runs/index-page-key
    u.a.n.r.witnesses/index-page-key]})

(defsc Show
  [_this {::m.n.relays/keys [address id]
          ::j.n.relays/keys [connection-count]
          :ui/keys          [admin-nav-menu admin-router]
          :as               props}]
  {:ident         ::m.n.relays/id
   :initial-state (fn [props]
                    (let [id (get props model-key)]
                      {model-key                     id
                       ::m.n.relays/id               nil
                       ::m.n.relays/address          ""
                       ::j.n.relays/connection-count 0
                       :ui/admin-nav-menu            (comp/get-initial-state u.menus/NavMenu
                                                       {::m.navbars/id show-menu-id
                                                        :id            id})
                       :ui/admin-router              (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
                     :ui/admin-router   [Router {}]})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   ::j.n.relays/connection-count
                   {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/admin-router (comp/get-query Router)}]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :Show/starting {:props props})
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/dl {}
            (dom/dt {} "Address")
            (dom/dd {} (str address))
            (dom/dt {} "Connections")
            (dom/dd {} (str connection-count)))
          (when log-props?
            (u.debug/log-props props)))
        (if admin-nav-menu
          (u.menus/ui-nav-menu admin-nav-menu)
          (u.debug/load-error props "admin show relay nav menu"))
        (if admin-router
          (ui-router admin-router)
          (u.debug/load-error props "admin show relay router"))))
    (u.debug/load-error props "admin show relay")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["relays"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show {})}])
   :route-segment ["relay" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (get props model-key)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin relays target"))
    (u.debug/load-error props "admin relays page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Relays"
   ::m.navlinks/description   "Admin index of relays"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Relay"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/navigate-key  u.a.n.r.connections/index-page-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
