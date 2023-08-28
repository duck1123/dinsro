(ns dinsro.ui.admin.core.nodes
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
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.nodes :as mu.c.nodes]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.core.nodes.blocks :as u.a.c.n.blocks]
   [dinsro.ui.admin.core.nodes.peers :as u.a.c.n.peers]
   [dinsro.ui.admin.core.nodes.transactions :as u.a.c.n.transactions]
   [dinsro.ui.admin.core.nodes.wallets :as u.a.c.n.wallets]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/nodes.cljc]]
;; [[../../../model/core/nodes.cljc]]

(def index-page-id :admin-core-nodes)
(def model-key ::m.c.nodes/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-nodes-show)

(def debug-props false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.nodes/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.nodes/fetch!))

(def button-info
  [{:label "fetch" :action mu.c.nodes/fetch!}
   {:label "fetch peers" :action mu.c.nodes/fetch-peers!}
   {:label "generate" :action mu.c.nodes/generate!}])

(defsc ActionsMenuItem
  [this {:keys [label mutation id]}]
  (ui-dropdown-item
   {:text    label
    :onClick #(comp/transact! this [(mutation {model-key id})])}))

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
  {:router-targets [u.a.c.n.blocks/SubPage
                    u.a.c.n.peers/SubPage
                    u.a.c.n.transactions/SubPage
                    u.a.c.n.wallets/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.c.n.peers/index-page-id
    u.a.c.n.blocks/index-page-id
    u.a.c.n.transactions/index-page-id
    u.a.c.n.wallets/index-page-id]})

(form/defsc-form EditForm
  [this props]
  {fo/attributes    [m.c.nodes/name
                     m.c.nodes/host
                     m.c.nodes/port
                     m.c.nodes/network
                     m.c.nodes/rpcuser
                     m.c.nodes/rpcpass]
   fo/field-options {::m.c.nodes/network u.pickers/network-picker}
   fo/field-styles  {::m.c.nodes/network :pick-one}
   fo/id            m.c.nodes/id
   fo/route-prefix  "edit-node"
   fo/title         "Edit Node"}
  (log/info :EditForm/starting {:props props})
  (form/render-layout this props))

(def ui-edit-form (comp/factory EditForm))

(defsc Show
  "Show a core node"
  [_this {::m.c.nodes/keys [id name network host port rpcuser rpcpass pruned?]
          :ui/keys         [admin-edit-form admin-editing? admin-nav-menu admin-router]
          :as              props}]
  {:ident         ::m.c.nodes/id
   :initial-state (fn [props]
                    (let [id (::m.c.nodes/id props)]
                      {::m.c.nodes/id                      nil
                       ::m.c.nodes/name                    ""
                       ::m.c.nodes/host                    ""
                       ::m.c.nodes/port                    0
                       ::m.c.nodes/rpcuser                 ""
                       ::m.c.nodes/rpcpass                 ""
                       ::m.c.nodes/pruned?                 true
                       ::m.c.nodes/difficulty              0
                       ::m.c.nodes/size-on-disk            0
                       ::m.c.nodes/initial-block-download? true
                       ::m.c.nodes/network                 (comp/get-initial-state u.links/NetworkLinkForm)
                       :ui/admin-edit-form                 (comp/get-initial-state EditForm)
                       :ui/admin-editing?                  false
                       :ui/admin-nav-menu                  (comp/get-initial-state u.menus/NavMenu
                                                             {::m.navbars/id show-page-id
                                                              :id            id})
                       :ui/admin-router                    (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-edit-form [EditForm {}]
                     :ui/admin-nav-menu  [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/admin-router    [Router {}]})
   :query         [::m.c.nodes/id
                   ::m.c.nodes/name
                   ::m.c.nodes/host
                   ::m.c.nodes/port
                   ::m.c.nodes/rpcuser
                   ::m.c.nodes/rpcpass
                   ::m.c.nodes/pruned?
                   ::m.c.nodes/difficulty
                   ::m.c.nodes/size-on-disk
                   ::m.c.nodes/initial-block-download?
                   {::m.c.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {:ui/admin-edit-form (comp/get-query EditForm)}
                   :ui/admin-editing?
                   {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/admin-router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (ui-actions-menu {model-key id})
          (if admin-editing?
            (ui-edit-form admin-edit-form)
            (dom/dl {}
              (dom/dt {} "Name")
              (dom/dd {} (str name))
              (dom/dt {} "Network")
              (dom/dd {} (u.links/ui-network-link network))
              (dom/dt {} "Host")
              (dom/dd {} host)
              (dom/dt {} "RPC User")
              (dom/dd {} rpcuser)
              (dom/dt {} "RPC Pass")
              (dom/dd {} rpcpass)
              (dom/dt {} "Port")
              (dom/dd {} port)
              (dom/dt {} "Pruned?")
              (dom/dd {} (str pruned?))))
          (when debug-props
            (u.debug/log-props (dissoc props :ui/nav-menu)))
          (ui-button {:onClick (fn [evt] (log/info :Show/edit-clicked {:id id :evt evt}))} "Edit"))
        (if admin-nav-menu
          (u.menus/ui-nav-menu admin-nav-menu)
          (u.debug/load-error props "admin core nodes show menu"))
        (if admin-router
          (ui-router admin-router)
          (u.debug/load-error props "admin core nodes show router"))))
    (u.debug/load-error props "admin core nodes show")))

(def ui-show (comp/factory Show))

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
  {ro/column-formatters {::m.c.nodes/name    #(u.links/ui-admin-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-admin-network-link %2)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action delete-action]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/admin-index
   ro/title             "Core Node Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["nodes"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     ::m.navlinks/id     show-page-id
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "Admin show core nodes target"))
    (u.debug/load-error props "Admin show core nodes page")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Nodes"
   o.navlinks/description   "Admin Index Core Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Node"
   o.navlinks/model-key     model-key
   o.navlinks/navigate-key  u.a.c.n.blocks/index-page-id
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
