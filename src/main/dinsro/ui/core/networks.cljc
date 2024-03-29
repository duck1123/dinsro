(ns dinsro.ui.core.networks
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
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.core.networks.blocks :as u.c.n.blocks]
   [dinsro.ui.core.networks.ln-nodes :as u.c.n.ln-nodes]
   [dinsro.ui.core.networks.nodes :as u.c.n.nodes]
   [dinsro.ui.core.networks.wallets :as u.c.n.wallets]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/networks.cljc]]
;; [[../../model/core/networks.cljc]]

(def index-page-id :core-networks)
(def model-key ::m.c.networks/id)
(def parent-router-id :core)
(def required-role :user)
(def show-page-id :core-networks-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.c.n.addresses/SubPage
    u.c.n.blocks/SubPage
    u.c.n.nodes/SubPage
    u.c.n.ln-nodes/SubPage
    u.c.n.wallets/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.c.n.addresses/index-page-id
    u.c.n.blocks/index-page-id
    u.c.n.ln-nodes/index-page-id
    u.c.n.nodes/index-page-id
    u.c.n.wallets/index-page-id]})

(defsc Show
  [_this {::m.c.networks/keys [id chain name]
          :ui/keys            [nav-menu router]
          :as                 props}]
  {:ident         ::m.c.networks/id
   :initial-state (fn [props]
                    (let [id (::m.c.networks/id props)]
                      {::m.c.networks/id    nil
                       ::m.c.networks/name  ""
                       ::m.c.networks/chain {}
                       :ui/nav-menu         (comp/get-initial-state u.menus/NavMenu {::m.navbars/id show-page-id :id id})
                       :ui/router           (comp/get-initial-state Router)}))
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/router   [Router {}]
                         :ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]})
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "Chain")
          (dom/dd {} (if chain (u.links/ui-chain-link chain) "None"))))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))
    (ui-segment {:color "red" :inverted true}
      (dom/h3 {} "Network Not loaded")
      (u.debug/ui-props-logger props))))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                         ::m.c.networks/name  #(u.links/ui-network-link %3)}
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
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["networks"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id show-page-id
                     o.navlinks/target      (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["network" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute   :core-networks-show
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Network"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    u.c.n.addresses/index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
