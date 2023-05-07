(ns dinsro.ui.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.menus :as me]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.ln.nodes :as mu.ln]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.nodes.accounts :as u.ln.n.accounts]
   [dinsro.ui.ln.nodes.addresses :as u.ln.n.addresses]
   [dinsro.ui.ln.nodes.channels :as u.ln.n.channels]
   [dinsro.ui.ln.nodes.peers :as u.ln.n.peers]
   [dinsro.ui.ln.nodes.remote-nodes :as u.ln.n.remote-nodes]
   [dinsro.ui.ln.nodes.transactions :as u.ln.n.transactions]
   [dinsro.ui.ln.nodes.wallet-addresses :as u.ln.n.wallet-addresses]))

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
   fo/field-options {::m.ln.nodes/core-node
                     {::picker-options/query-key       ::m.c.nodes/index
                      ::picker-options/query-component u.links/CoreNodeLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.c.nodes/keys [id name]}]
                           {:text  (str name)
                            :value [::m.c.nodes/id id]})
                         (sort-by ::m.c.nodes/name options)))}
                     ::m.ln.nodes/user
                     {::picker-options/query-key       ::m.users/index
                      ::picker-options/query-component u.links/UserLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.users/keys [id name]}]
                           {:text  (str name)
                            :value [::m.users/id id]})
                         (sort-by ::m.users/name options)))}}
   fo/field-styles  {::m.ln.nodes/core-node :pick-one
                     ::m.ln.nodes/user      :pick-one}
   fo/id            m.ln.nodes/id
   fo/route-prefix  "create-node"
   fo/title         "Create Lightning Node"}
  (if override-create-form
    (form/render-layout this props)
    (dom/div :.ui.grid
      (dom/div :.row
        (dom/div :.sixteen.wide.column
          (dom/div {}
            (form/render-layout this props)))))))

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

(defsc Show
  "Show a ln node"
  [this {:ui/keys          [router]
         ::m.ln.nodes/keys [id user core-node host port hasCert? hasMacaroon? network]
         :as               props}]
  {:ident         ::m.ln.nodes/id
   :initial-state {::m.ln.nodes/id           nil
                   ::m.ln.nodes/user         {}
                   ::m.ln.nodes/network      {}
                   ::m.ln.nodes/core-node    {}
                   ::m.ln.nodes/host         ""
                   ::m.ln.nodes/port         0
                   ::m.ln.nodes/hasCert?     false
                   ::m.ln.nodes/hasMacaroon? false
                   :ui/router                {}}
   :pre-merge     (u.links/page-merger ::m.ln.nodes/id {:ui/router Router})
   :query         [::m.ln.nodes/id
                   ::m.ln.nodes/host
                   ::m.ln.nodes/port
                   ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?
                   {::m.ln.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.ln.nodes/user (comp/get-query u.links/UserLinkForm)}
                   {::m.ln.nodes/core-node (comp/get-query u.links/CoreNodeLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["nodes" :id]
   :will-enter    (partial u.links/page-loader ::m.ln.nodes/id ::Show)}
  (dom/div {}
    (dom/div :.ui.segment
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
                           :onClick #(comp/transact! this [(mu.ln/download-cert! {::m.ln.nodes/id id})])}
                "Fetch"))))
        (dom/div :.item
          (dom/div :.header "Has Macaroon?")
          (if hasMacaroon?
            (str hasMacaroon?)
            (dom/a {:onClick #(comp/transact! this [(mu.ln/download-macaroon! {::m.ln.nodes/id id})])}
              (str hasMacaroon?))))))
    (u.links/ui-nav-menu {:id id :menu-items me/ln-nodes-menu-items})
    (if router
      (ui-router router)
      (dom/div :.ui.segment
        (dom/h3 {} "Network Router not loaded")
        (u.links/ui-props-logger props)))))

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
   ro/route             "nodes"
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "Lightning Node Report"})

(def ui-report (comp/factory Report))
