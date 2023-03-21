(ns dinsro.ui.core.nodes
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.core.nodes.blocks :as u.c.n.blocks]
   [dinsro.ui.core.nodes.peers :as u.c.n.peers]
   [dinsro.ui.links :as u.links]))

(def button-info
  [{:label "fetch" :action mu.c.nodes/fetch!}
   {:label "fetch peers" :action mu.c.nodes/fetch-peers!}
   {:label "generate" :action mu.c.nodes/generate!}])

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {::m.c.nodes/id id})])}))

(def ui-actions-menu-item (comp/factory ActionsMenuItem {:keyfn :label}))

(defsc ActionsMenu
  [_this {::m.c.nodes/keys [id]}]
  {:initial-state {::m.c.nodes/id nil}
   :query         [::m.c.nodes/id]}
  (ui-dropdown
   {:icon    "settings"
    :button  true
    :labeled false}
   (ui-dropdown-menu
    {}
    (for [{:keys [label action]} button-info]
      (ui-actions-menu-item {:label label :mutation action :id id})))))

(def ui-actions-menu
  "node actions menu"
  (comp/factory ActionsMenu))

(defrouter Router
  [_this _props]
  {:router-targets [u.c.n.blocks/SubPage
                    u.c.n.peers/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "peers"
    :name  "Peers"
    :route "dinsro.ui.core.nodes.peers/SubPage"}
   {:key   "blocks"
    :name  "Blocks"
    :route "dinsro.ui.core.nodes.blocks/SubPage"}])

(defsc Show
  "Show a core node"
  [_this {::m.c.nodes/keys [id name network]
          :ui/keys         [router]
          :as              props}]
  {:route-segment ["node" :id]
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.c.nodes/id      nil
                   ::m.c.nodes/name    ""
                   ::m.c.nodes/network {}
                   :ui/router          {}}
   :ident         ::m.c.nodes/id
   :pre-merge (u.links/page-merger ::m.c.nodes/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.c.nodes/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (ui-actions-menu {::m.c.nodes/id id})
          (dom/dl {}
            (dom/dt {} "Name")
            (dom/dd {} (str name))
            (dom/dt {} "Network")
            (dom/dd {} (u.links/ui-network-link network))))
        (u.links/ui-nav-menu {:menu-items menu-items :id id})
        (if router
          (ui-router router)
          (dom/div :.ui.segment
            (dom/h3 {} "Network Router not loaded")
            (u.links/ui-props-logger props)))))
    (dom/div :.ui.segment
      (dom/h3 {} "Node not loaded")
      (u.links/ui-props-logger props))))

(form/defsc-form NewForm [_this _props]
  {fo/id           m.c.nodes/id
   fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["nodes"]
   fo/route-prefix "new-core-node"
   fo/title        "Core Node"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/row-actions       [(u.links/row-action-button "Fetch" ::m.c.nodes/id mu.c.nodes/fetch!)
                         (u.links/row-action-button "Delete" ::m.c.nodes/id mu.c.nodes/delete!)]
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Node Report"
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/route             "nodes"})
