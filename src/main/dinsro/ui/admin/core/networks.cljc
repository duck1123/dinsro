(ns dinsro.ui.admin.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.admin.core.networks.addresses :as u.a.c.n.addresses]
   [dinsro.ui.admin.core.networks.blocks :as u.a.c.n.blocks]
   [dinsro.ui.admin.core.networks.ln-nodes :as u.a.c.n.ln-nodes]
   [dinsro.ui.admin.core.networks.nodes :as u.a.c.n.nodes]
   [dinsro.ui.admin.core.networks.wallets :as u.a.c.n.wallets]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/networks.cljc]]
;; [[../../../model/core/networks.cljc]]

(def index-page-id :admin-core-networks)
(def model-key ::m.c.networks/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-networks-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.c.n.addresses/SubPage
    u.a.c.n.blocks/SubPage
    u.a.c.n.nodes/SubPage
    u.a.c.n.ln-nodes/SubPage
    u.a.c.n.wallets/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.c.n.addresses/index-page-id
    u.a.c.n.blocks/index-page-id
    u.a.c.n.ln-nodes/index-page-id
    u.a.c.n.nodes/index-page-id
    u.a.c.n.wallets/index-page-id]})

(defsc Show
  [_this {::m.c.networks/keys [id chain name]
          :ui/keys            [nav-menu router]
          :as                 props}]
  {:ident         ::m.c.networks/id
   :initial-state (fn [props]
                    (let [id (::m.c.networks/id props)]
                      {model-key            nil
                       ::m.c.networks/name  ""
                       ::m.c.networks/chain {}
                       :ui/nav-menu         (comp/get-initial-state u.menus/NavMenu
                                              {::m.navbars/id show-page-id
                                               :id            id})
                       :ui/router           (comp/get-initial-state Router)
                       :ui/editing?         false}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}
                   :ui/editing?]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "Chain")
          (dom/dd {} (if chain (u.links/ui-chain-link chain) "None"))))
      (if nav-menu
        (u.menus/ui-nav-menu nav-menu)
        (u.debug/load-error props "admin networks show menu"))
      (if router
        (ui-router router)
        (u.debug/load-error props "admin networks show router")))
    (u.debug/load-error props "admin network show")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.networks/chain #(u.links/ui-admin-chain-link %2)
                         ::m.c.networks/name  #(u.links/ui-admin-network-link %3)}
   ro/columns           [m.c.networks/name
                         m.c.networks/chain]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.networks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.networks/index
   ro/title             "Networks"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["networks"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.c.networks/keys [id]
          ::m.navlinks/keys   [target]
          :as                 props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     ::m.navlinks/id     show-page-id
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show {})}])
   :route-segment ["network" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props)))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin index networks"
   ::m.navlinks/label         "Networks"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/required-role required-role
   ::m.navlinks/router        parent-router-id})

(m.navlinks/defroute show-page-id
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin Show Network"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Network"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/navigate-key  u.a.c.n.addresses/index-page-id
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/required-role required-role
   ::m.navlinks/router        parent-router-id})
