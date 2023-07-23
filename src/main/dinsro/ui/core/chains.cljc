(ns dinsro.ui.core.chains
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
   [dinsro.ui.core.chains.networks :as u.c.c.networks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/chains.cljc]]
;; [[../../model/core/chains.cljc]]

(def index-page-key :core-chains)
(def model-key ::m.c.chains/id)
(def show-page-key :core-chains-show)

(def override-form false)

(form/defsc-form NewForm
  [this props]
  {fo/attributes   [m.c.chains/name]
   fo/cancel-route ["chains"]
   fo/id           m.c.chains/id
   fo/route-prefix "chain"
   fo/title        "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (form/render-layout this props))))

(defrouter Router
  [_this _props]
  {:router-targets [u.c.c.networks/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu
  :core-chains
  {::m.navbars/parent :core
   ::m.navbars/router ::Router
   ::m.navbars/children
   [:core-chains-show-networks]})

(defsc Show
  [_this {::m.c.chains/keys [name]
          :ui/keys          [nav-menu router]
          :as               props}]
  {:ident         ::m.c.chains/id
   :initial-state (fn [props]
                    (let [id (::m.c.chains/id props)]
                      {::m.c.chains/id   nil
                       ::m.c.chains/name ""
                       :ui/nav-menu
                       (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :core-chains
                                                                :id            id})
                       :ui/router        (comp/get-initial-state Router)}))
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/router   [Router {}]
                         :ui/nav-menu [u.menus/NavMenu {::m.navbars/id :core-chains}]})
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/debug :Show/starting {:props props})
  (dom/div {}
    (ui-segment {}
      (dom/h1 {} "Show Chain")
      (dom/dl {}
        (dom/dt {} "Name")
        (dom/dd {} (str name))))
    (u.menus/ui-nav-menu nav-menu)
    (if router
      (ui-router router)
      (ui-segment {}
        (dom/h3 {} "Chain Router not loaded")
        (u.debug/ui-props-logger props)))))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.chains/name #(u.links/ui-chain-link %3)}
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
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["chains"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.c.chains/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.c.chains/id     nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.c.chains/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["chain" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show chains")))

(m.navlinks/defroute   :core-chains-show
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Chain"
   ::m.navlinks/model-key     ::m.c.chains/id
   ::m.navlinks/navigate-key  :core-chains-show-networks
   ::m.navlinks/parent-key    :core
   ::m.navlinks/router        :core
   ::m.navlinks/required-role :user})
