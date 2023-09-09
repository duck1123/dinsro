(ns dinsro.ui.settings.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
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
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.ln.nodes.accounts :as u.s.ln.n.accounts]
   [dinsro.ui.settings.ln.nodes.addresses :as u.s.ln.n.addresses]
   [dinsro.ui.settings.ln.nodes.channels :as u.s.ln.n.channels]
   [dinsro.ui.settings.ln.nodes.peers :as u.s.ln.n.peers]
   [dinsro.ui.settings.ln.nodes.remote-nodes :as u.s.ln.n.remote-nodes]
   [dinsro.ui.settings.ln.nodes.transactions :as u.s.ln.n.transactions]
   [dinsro.ui.settings.ln.nodes.wallet-addresses :as u.s.ln.n.wallet-addresses]
   [lambdaisland.glogc :as log]))

(def index-page-id :settings-ln-nodes)
(def model-key ::m.ln.nodes/id)
(def parent-router-id :settings-ln)
(def required-role :user)
(def show-page-id :settings-ln-nodes-show)

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

(defrouter Router
  [_this _props]
  {:router-targets
   [u.s.ln.n.accounts/SubPage
    u.s.ln.n.addresses/SubPage
    u.s.ln.n.channels/SubPage
    u.s.ln.n.peers/SubPage
    u.s.ln.n.remote-nodes/SubPage
    u.s.ln.n.transactions/SubPage
    u.s.ln.n.wallet-addresses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.s.ln.n.accounts/index-page-id
    u.s.ln.n.addresses/index-page-id
    u.s.ln.n.channels/index-page-id
    u.s.ln.n.peers/index-page-id
    u.s.ln.n.remote-nodes/index-page-id
    u.s.ln.n.transactions/index-page-id
    u.s.ln.n.wallet-addresses/index-page-id]})

(defsc Show
  "Show a ln node"
  [this {:ui/keys          [nav-menu router]
         ::m.ln.nodes/keys [id user core-node host port hasCert? hasMacaroon? network]
         :as               props}]
  {:ident         ::m.ln.nodes/id
   :initial-state (fn [props]
                    (let [id (::m.ln.nodes/id props)]
                      {::m.ln.nodes/id           nil
                       ::m.ln.nodes/user         {}
                       ::m.ln.nodes/network      {}
                       ::m.ln.nodes/core-node    {}
                       ::m.ln.nodes/host         ""
                       ::m.ln.nodes/port         0
                       ::m.ln.nodes/hasCert?     false
                       ::m.ln.nodes/hasMacaroon? false
                       :ui/nav-menu              (comp/get-initial-state u.menus/NavMenu {::m.navbars/id index-page-id :id id})
                       :ui/router                (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger ::m.ln.nodes/id
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id index-page-id}]
                     :ui/router   [Router {}]})
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
  (dom/div {}
    (ui-segment {}
      (ui-actions-menu
       {::m.ln.nodes/id           id
        ::m.ln.nodes/hasCert?     hasCert?
        ::m.ln.nodes/hasMacaroon? hasMacaroon?})
      (dom/div :.ui.list
        (dom/div :.item
          (dom/div :.header "User")
          (u.links/ui-user-link user))
        (dom/div :.item
          (dom/div :.header "Core Node")
          (u.links/ui-core-node-link core-node))
        (dom/div :.item
          (dom/div :.header "Address")
          host ":" (str port))
        (dom/div :.item
          (dom/div :.header "Network")
          (u.links/ui-network-link network))
        (dom/div :.item
          (dom/div :.header "Has Cert?")
          (str hasCert?)
          (when-not hasCert?
            (dom/div {}
              (dom/p {} "Cert not found")
              (dom/button {:classes [:.ui.button]
                           :onClick #(comp/transact! this [`(mu.ln/download-cert! {::m.ln.nodes/id ~id})])}
                "Fetch"))))
        (dom/div :.item
          (dom/div :.header "Has Macaroon?")
          (if hasMacaroon?
            (str hasMacaroon?)
            (dom/a {:onClick #(comp/transact! this [`(mu.ln/download-macaroon! {::m.ln.nodes/id ~id})])}
              (str hasMacaroon?))))))
    (when nav-menu (u.menus/ui-nav-menu nav-menu))
    (if router
      (ui-router router)
      (ui-segment {}
        (dom/h3 {} "Network Router not loaded")
        (u.debug/ui-props-logger props)))))

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
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["nodes"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "settings ln nodes"))))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
