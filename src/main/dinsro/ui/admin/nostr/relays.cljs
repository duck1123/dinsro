(ns dinsro.ui.admin.nostr.relays
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.admin.nostr.relays.connections :as u.a.n.r.connections]
   [dinsro.ui.admin.nostr.relays.events :as u.a.n.r.events]
   [dinsro.ui.admin.nostr.relays.pubkeys :as u.a.n.r.pubkeys]
   [dinsro.ui.admin.nostr.relays.requests :as u.a.n.r.requests]
   [dinsro.ui.admin.nostr.relays.runs :as u.a.n.r.runs]
   [dinsro.ui.admin.nostr.relays.witnesses :as u.a.n.r.witnesses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props   (comp/props this)
                   address (::m.n.relays/address props)]
               (log/info :submit-button/clicked {:address address})
               (comp/transact!
                this
                [(mu.n.relays/submit!
                  {::m.n.relays/address address})])))})

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
                         ::j.n.relays/connection-count #(u.links/ui-relay-connection-count-link %3)
                         ::j.n.relays/request-count    #(u.links/ui-relay-request-count-link %3)}
   ro/columns           [m.n.relays/address
                         m.n.relays/connected
                         j.n.relays/request-count
                         j.n.relays/connection-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "relays"
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.n.relays/id mu.n.relays/delete!)]
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.r.connections/SubPage
    u.a.n.r.pubkeys/SubPage
    u.a.n.r.requests/SubPage
    u.a.n.r.runs/SubPage
    u.a.n.r.events/SubPage
    u.a.n.r.witnesses/SubPage]})

(defsc Show
  [_this {::m.n.relays/keys [address]
          ::j.n.relays/keys [connection-count]
          :ui/keys          [nav-menu router]}]
  {:ident         ::m.n.relays/id
   :initial-state
   (fn [props]
     (let [id (::m.n.relays/id props)]
       {::m.n.relays/id               nil
        ::m.n.relays/address          ""
        ::j.n.relays/connection-count 0
        :ui/nav-menu
        (comp/get-initial-state u.menus/NavMenu
                                {::m.navbars/id :admin-nostr-relays :id id})
        :ui/router                    (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger
                   ::m.n.relays/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :admin-nostr-relays}]
                    :ui/router [Router {}]})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   ::j.n.relays/connection-count
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["relay" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.relays/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "Address")
          (dom/dd {} (str address))
          (dom/dt {} "Connections")
          (dom/dd {} (str connection-count))))
      (u.menus/ui-nav-menu nav-menu)
      ((comp/factory Router) router))))
