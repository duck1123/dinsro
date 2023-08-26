(ns dinsro.ui.admin.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.admin.rate-sources.accounts :as u.a.rs.accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../joins/rate_sources.cljc]]
;; [[../../model/rate_sources.cljc]]
;; [[../../mutations/rate_sources.cljc]]
;; [[../../ui/settings/rate_sources.cljs]]

(def index-page-id :admin-rate-sources)
(def model-key ::m.rate-sources/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-rate-sources-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.rate-sources/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rate-sources/name #(u.links/ui-admin-rate-source-link %3)}
   ro/columns           [m.rate-sources/name
                         j.rate-sources/rate-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.rate-sources/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rate-sources/admin-index
   ro/title             "Rate Sources"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.rs.accounts/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.a.rs.accounts/index-page-id]})

(defsc Show
  [_this {::m.rate-sources/keys [id name url active currency]
          :ui/keys              [nav-menu router]
          :as                   props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             ::m.rate-sources/id
   :initial-state     (fn [props]
                        (let [id (::m.rate-sources/id props)]
                          {::m.rate-sources/name     ""
                           ::m.rate-sources/id       nil
                           ::m.rate-sources/active   false
                           ::m.rate-sources/currency {}
                           ::m.rate-sources/url      ""
                           :ui/nav-menu              (comp/get-initial-state u.menus/NavMenu
                                                       {::m.navbars/id show-page-id
                                                        :id            id})
                           :ui/router                (comp/get-initial-state Router)}))
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                         :ui/router   [Router {}]})
   :query             [::m.rate-sources/name
                       ::m.rate-sources/url
                       {::m.rate-sources/currency (comp/get-query u.links/CurrencyLinkForm)}
                       ::m.rate-sources/active
                       ::m.rate-sources/id
                       {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                       {:ui/router (comp/get-query Router)}]}
  (if id
    (dom/div {}
      (ui-container {:fluid true}
        (ui-segment {}
          (dom/h1 {} (str name))
          (dom/p {} "Url: " (str url))
          (dom/p {} "Active: " (str (boolean active)))
          (dom/p {} "Currency: " (u.links/ui-currency-link currency))))
      (ui-container {}
        (if nav-menu
          (u.menus/ui-nav-menu nav-menu)
          (u.debug/load-error props "admin rate sources nav menu"))
        (if router
          (ui-router router)
          (u.debug/load-error props "admin rate sources router"))))
    (u.debug/load-error props "admin rate sources")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["rate-sources"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.navlinks/id     show-page-id
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["rate-source" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (u.debug/load-error props "admin show rate source page")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Rate Sources"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show page for rate sources"
   ::m.navlinks/label         "Show Rate Source"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
