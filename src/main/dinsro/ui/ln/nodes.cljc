(ns dinsro.ui.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-header :refer [ui-list-header]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.nodes.accounts :as u.ln.n.accounts]
   [dinsro.ui.ln.nodes.addresses :as u.ln.n.addresses]
   [dinsro.ui.ln.nodes.channels :as u.ln.n.channels]
   [dinsro.ui.ln.nodes.peers :as u.ln.n.peers]
   [dinsro.ui.ln.nodes.remote-nodes :as u.ln.n.remote-nodes]
   [dinsro.ui.ln.nodes.transactions :as u.ln.n.transactions]
   [dinsro.ui.ln.nodes.wallet-addresses :as u.ln.n.wallet-addresses]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../joins/ln/nodes.cljc]]
;; [[../../model/ln/nodes.cljc]]

(def index-page-id :ln-nodes)
(def model-key ::m.ln.nodes/id)
(def parent-router-id :ln)
(def required-role :user)
(def show-page-id :ln-nodes-show)

(declare CreateLightningNodeForm)

(def new-node-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this CreateLightningNodeForm))})

(def button-info
  [{:label            "Unlock"
    :action           mu.ln/unlock!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Initialize"
    :action           mu.ln/initialize!
    :requiresCert     true
    :requiresMacaroon false}
   {:label            "Update Info"
    :action           mu.ln/update-info!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Invoices"
    :action           mu.ln/fetch-invoices!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Payments"
    :action           mu.ln/fetch-payments!
    :requiresCert     true
    :requiresMacaroon true}
   {:label            "Fetch Transactions"
    :action           mu.ln/fetch-transactions!
    :requiresCert     true
    :requiresMacaroon true}])

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {::m.ln.nodes/id id})])}))

(def ui-actions-menu-item (comp/factory ActionsMenuItem {:keyfn :label}))

(defsc ActionsMenu
  [_this {::m.ln.nodes/keys [id hasCert? hasMacaroon?]}]
  {:initial-state {::m.ln.nodes/id           nil
                   ::m.ln.nodes/hasCert?     false
                   ::m.ln.nodes/hasMacaroon? false}
   :query         [::m.ln.nodes/id ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?]}
  (ui-dropdown
   {:icon    "settings"
    :button  true
    :labeled false}
   (ui-dropdown-menu
    {}
    (for [{:keys [label action hideCert hideMacaroon requiresCert requiresMacaroon]} button-info]
      (let [cert-hidden     (and hasCert? hideCert)
            macaroon-hidden (and hasMacaroon? hideMacaroon)
            hidden          (or cert-hidden macaroon-hidden)]
        (when (not (or hidden
                       (and requiresCert (not hasCert?))
                       (and requiresMacaroon (not hasMacaroon?))))
          (ui-actions-menu-item {:label label :mutation action :id id})))))))

(def ui-actions-menu (comp/factory ActionsMenu))

(def override-create-form false)

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.ln.nodes/name
                     m.ln.nodes/host
                     m.ln.nodes/port
                     m.ln.nodes/core-node
                     m.ln.nodes/user]
   fo/cancel-route  ["nodes"]
   fo/field-options {::m.ln.nodes/core-node u.pickers/core-node-picker
                     ::m.ln.nodes/user      u.pickers/user-picker}
   fo/field-styles  {::m.ln.nodes/core-node :pick-one
                     ::m.ln.nodes/user      :pick-one}
   fo/id            m.ln.nodes/id
   fo/route-prefix  "create-node"
   fo/title         "Create Lightning Node"}
  (if override-create-form
    (form/render-layout this props)
    (ui-grid {}
      (ui-grid-row {}
        (ui-grid-column {:width 16}
          (form/render-layout this props))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.ln.n.accounts/SubPage
    u.ln.n.addresses/SubPage
    u.ln.n.channels/SubPage
    u.ln.n.peers/SubPage
    u.ln.n.remote-nodes/SubPage
    u.ln.n.transactions/SubPage
    u.ln.n.wallet-addresses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.ln.n.accounts/index-page-id
    u.ln.n.addresses/index-page-id
    u.ln.n.channels/index-page-id
    u.ln.n.peers/index-page-id
    u.ln.n.remote-nodes/index-page-id
    u.ln.n.wallet-addresses/index-page-id]})

(defsc Show
  "Show a ln node"
  [this {:ui/keys          [nav-menu router]
         ::m.ln.nodes/keys [id user core-node host port hasCert? hasMacaroon? network]
         :as               props}]
  {:ident         ::m.ln.nodes/id
   :initial-state (fn [props]
                    (log/trace :Show/initial-state {:props props})
                    (let [id (::m.ln.nodes/id props)]
                      {::m.ln.nodes/id           nil
                       ::m.ln.nodes/user         {}
                       ::m.ln.nodes/network      {}
                       ::m.ln.nodes/core-node    {}
                       ::m.ln.nodes/host         ""
                       ::m.ln.nodes/port         0
                       ::m.ln.nodes/hasCert?     false
                       ::m.ln.nodes/hasMacaroon? false
                       :ui/router                (comp/get-initial-state Router)
                       :ui/nav-menu              (comp/get-initial-state u.menus/NavMenu
                                                   {::m.navbars/id show-page-id
                                                    :id            id})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/router   [Router {}]
                     :ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]})
   :query         [::m.ln.nodes/id
                   ::m.ln.nodes/host
                   ::m.ln.nodes/port
                   ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?
                   {::m.ln.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.ln.nodes/user (comp/get-query u.links/UserLinkForm)}
                   {::m.ln.nodes/core-node (comp/get-query u.links/CoreNodeLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (ui-actions-menu
         {::m.ln.nodes/id           id
          ::m.ln.nodes/hasCert?     hasCert?
          ::m.ln.nodes/hasMacaroon? hasMacaroon?})
        (dom/div :.ui.list
          (ui-list-item {}
            (ui-list-header {} "User")
            (u.links/ui-user-link user))
          (ui-list-item {}
            (ui-list-header {} "Core Node")
            (u.links/ui-core-node-link core-node))
          (ui-list-item {}
            (ui-list-header {} "Address")
            host ":" (str port))
          (ui-list-item {}
            (ui-list-header {} "Network")
            (u.links/ui-network-link network))
          (ui-list-item {}
            (ui-list-header {} "Has Cert?")
            (str hasCert?)
            (when-not hasCert?
              (dom/div {}
                (dom/p {} "Cert not found")
                (ui-button {:onClick #(comp/transact! this [`(mu.ln/download-cert! {::m.ln.nodes/id ~id})])}
                           "Fetch"))))
          (dom/div :.item
            (dom/div :.header "Has Macaroon?")
            (if hasMacaroon?
              (str hasMacaroon?)
              (dom/a {:onClick #(comp/transact! this [`(mu.ln/download-macaroon! {::m.ln.nodes/id ~id})])}
                (str hasMacaroon?))))))
      (when nav-menu
        (u.menus/ui-nav-menu nav-menu))
      (if router
        (ui-router router)
        (u.debug/load-error props "ln node show router")))
    (u.debug/load-error props "ln node show")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.nodes/name      #(u.links/ui-node-link %3)
                         ::m.ln.nodes/network   #(u.links/ui-network-link %2)
                         ::m.ln.nodes/user      #(u.links/ui-user-link %2)
                         ::m.ln.nodes/core-node #(u.links/ui-core-node-link %2)}
   ro/columns           [m.ln.nodes/name
                         m.ln.nodes/network
                         m.ln.info/alias-attr
                         m.ln.nodes/core-node
                         m.ln.info/color
                         m.ln.nodes/user]
   ro/control-layout    {:action-buttons [::new-node ::refresh]}
   ro/controls          {::new-node new-node-button
                         ::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "Lightning Node Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["nodes"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Index LN Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Node"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
