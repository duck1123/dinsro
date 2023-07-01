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
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.nodes.blocks :as u.c.n.blocks]
   [dinsro.ui.core.nodes.peers :as u.c.n.peers]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

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

(defsc Show
  "Show a core node"
  [_this {::m.c.nodes/keys [id name network]
          :ui/keys         [nav-menu router]}]
  {:ident         ::m.c.nodes/id
   :initial-state
   (fn [props]
     (let [id (::m.c.nodes/id props)]
       {::m.c.nodes/id      nil
        ::m.c.nodes/name    ""
        ::m.c.nodes/network (comp/get-initial-state u.links/NetworkLinkForm)
        :ui/nav-menu        (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :core-nodes
                                                                     :id            id})
        :ui/router          (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger
                   ::m.c.nodes/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :core-nodes}]
                    :ui/router   [Router {}]})
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["node" :id]
   :will-enter    (partial u.loader/page-loader ::m.c.nodes/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (ui-actions-menu {::m.c.nodes/id id})
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "Network")
          (dom/dd {} (u.links/ui-network-link network))))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["nodes"]
   fo/id           m.c.nodes/id
   fo/route-prefix "new-core-node"
   fo/title        "Core Node"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "nodes"
   ro/row-actions       [(u.buttons/row-action-button "Fetch" ::m.c.nodes/id mu.c.nodes/fetch!)
                         (u.buttons/row-action-button "Delete" ::m.c.nodes/id mu.c.nodes/delete!)]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Node Report"})
