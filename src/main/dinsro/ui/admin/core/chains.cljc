(ns dinsro.ui.admin.core.chains
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
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.chains :as j.c.chains]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.admin.core.chains.networks :as u.a.c.c.networks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/chains.cljc]]
;; [[../../../model/core/chains.cljc]]

(def index-page-id :admin-core-chains)
(def model-key ::m.c.chains/id)
(def override-form false)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-chains-show)

(form/defsc-form NewForm
  [this props]
  {fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/id             m.c.chains/id
   fo/route-prefix   "chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (form/render-layout this props))))

(defrouter Router
  [_this _props]
  {:router-targets [u.a.c.c.networks/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.c.c.networks/index-page-id]})

(defsc Show
  [_this {::m.c.chains/keys [name]
          :ui/keys          [nav-menu router]
          :as               props}]
  {:ident         ::m.c.chains/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key         id
                       ::m.c.chains/name ""
                       :ui/nav-menu      (comp/get-initial-state u.menus/NavMenu
                                           {::m.navbars/id show-page-id
                                            :id            id})
                       :ui/router        (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/info :ShowPage/starting {:props props})
  (if (get props model-key)
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Show Chain")
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))))
      (if nav-menu
        (u.menus/ui-nav-menu nav-menu)
        (u.debug/load-error props "admin show chain menu"))
      (if router
        (ui-router router)
        (u.debug/load-error props "admin show chain router")))
    (u.debug/load-error props "admin show chain")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.chains/name #(u.links/ui-admin-chain-link %3)}
   ro/columns           [m.c.chains/name]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.chains/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.chains/index
   ro/title             "Chains"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["chains"]
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
   :route-segment ["chain" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (get props model-key)
    (ui-show target)
    (u.debug/load-error props "admin show chains")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin index chains"
   ::m.navlinks/label         "Chains"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show chain"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Chain"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
