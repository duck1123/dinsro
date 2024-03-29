(ns dinsro.ui.admin.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-header :refer [ui-list-header]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-list :refer [ui-list-list]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.ln.nodes.accounts :as u.a.ln.n.accounts]
   [dinsro.ui.admin.ln.nodes.addresses :as u.a.ln.n.addresses]
   [dinsro.ui.admin.ln.nodes.channels :as u.a.ln.n.channels]
   [dinsro.ui.admin.ln.nodes.peers :as u.a.ln.n.peers]
   [dinsro.ui.admin.ln.nodes.remote-nodes :as u.a.ln.n.remote-nodes]
   [dinsro.ui.admin.ln.nodes.transactions :as u.a.ln.n.transactions]
   [dinsro.ui.admin.ln.nodes.wallet-addresses :as u.a.ln.n.wallet-addresses]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin.ln.nodes :as u.f.a.ln.nodes]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/nodes.cljc]]
;; [[../../../model/ln/nodes.cljc]]
;; [[../../../ui/ln/nodes.cljc]]
;; [[../../../ui/admin/ln/nodes/accounts.cljs]]
;; [[../../../ui/admin/ln/nodes/addresses.cljc]]

(def index-page-id :admin-ln-nodes)
(def model-key ::m.ln.nodes/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-nodes-show)

(def new-node-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this u.f.a.ln.nodes/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.nodes/name      #(u.links/ui-admin-ln-node-link %3)
                         ::m.ln.nodes/user      #(u.links/ui-admin-user-link %2)
                         ::m.ln.nodes/core-node #(u.links/ui-admin-core-node-link %2)}
   ro/columns           [m.ln.nodes/name
                         m.ln.info/alias-attr
                         m.ln.nodes/core-node
                         m.ln.info/color
                         m.ln.nodes/user]
   ro/control-layout    {:action-buttons [::new-node]}
   ro/controls          {::new-node new-node-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "nodes"
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/admin-index
   ro/title             "Lightning Node Report"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.ln.n.accounts/SubPage
    u.a.ln.n.addresses/SubPage
    u.a.ln.n.channels/SubPage
    u.a.ln.n.peers/SubPage
    u.a.ln.n.remote-nodes/SubPage
    u.a.ln.n.transactions/SubPage
    u.a.ln.n.wallet-addresses/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  "Show a ln node"
  [this {:ui/keys          [admin-nav-menu admin-router]
         ::m.ln.nodes/keys [id user core-node host port hasCert? hasMacaroon? network]
         :as               props}]
  {:ident         ::m.ln.nodes/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {::m.ln.nodes/id           id
                       ::m.ln.nodes/user         {}
                       ::m.ln.nodes/network      {}
                       ::m.ln.nodes/core-node    {}
                       ::m.ln.nodes/host         ""
                       ::m.ln.nodes/port         0
                       ::m.ln.nodes/hasCert?     false
                       ::m.ln.nodes/hasMacaroon? false
                       :ui/admin-router          (comp/get-initial-state Router)
                       :ui/admin-nav-menu        (comp/get-initial-state u.menus/NavMenu
                                                   {::m.navbars/id show-page-id
                                                    :id            id})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-router   [Router {}]
                     :ui/admin-nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]})
   :query         [::m.ln.nodes/id
                   ::m.ln.nodes/host
                   ::m.ln.nodes/port
                   ::m.ln.nodes/hasCert?
                   ::m.ln.nodes/hasMacaroon?
                   {::m.ln.nodes/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.ln.nodes/user (comp/get-query u.links/UserLinkForm)}
                   {::m.ln.nodes/core-node (comp/get-query u.links/CoreNodeLinkForm)}
                   {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/admin-router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (dom/div {}
    (if id
      (ui-segment {}
        (u.ln.nodes/ui-actions-menu
         {::m.ln.nodes/id           id
          ::m.ln.nodes/hasCert?     hasCert?
          ::m.ln.nodes/hasMacaroon? hasMacaroon?})
        (ui-list-list {}
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
                (dom/button {:classes [:.ui.button]
                             :onClick #(comp/transact! this [`(mu.ln/download-cert! {::m.ln.nodes/id ~id})])}
                  "Fetch"))))
          (ui-list-item {}
            (ui-list-header {} "Has Macaroon?")
            (if hasMacaroon?
              (str hasMacaroon?)
              (dom/a {:onClick #(comp/transact! this [`(mu.ln/download-macaroon! {::m.ln.nodes/id ~id})])}
                (str hasMacaroon?))))))
      (ui-segment {} "Failed to load record"))
    (when admin-nav-menu (u.menus/ui-nav-menu admin-nav-menu))
    (if admin-router
      (ui-router admin-router)
      (u.debug/load-error props "admin ln nodes"))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["nodes"]
   :will-enter        (u.loader/page-loader index-page-id)}
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
   o.navlinks/label         "Nodes"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Node"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
