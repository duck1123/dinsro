(ns dinsro.ui.settings.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.settings.rate-sources :as u.f.s.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.settings.rate-sources.accounts :as u.s.rs.accounts]
   [dinsro.ui.settings.rate-sources.rates :as u.s.rs.rates]
   [lambdaisland.glogc :as log]))

;; [[../../joins/rate_sources.cljc]]
;; [[../../model/rate_sources.cljc]]
;; [[../../mutations/rate_sources.cljc]]
;; [[../../ui/admin/rate_sources.cljs]]

(def index-page-id :settings-rate-sources)
(def model-key ::m.rate-sources/id)
(def parent-router-id :settings)
(def required-role :user)
(def show-page-id :settings-rate-sources-show)

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.s.rate-sources/NewForm))})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.s.rs.accounts/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.s.rs.accounts/index-page-id]})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                         ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/columns           [m.rate-sources/name
                         m.rate-sources/url
                         m.rate-sources/active?
                         j.rate-sources/rate-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-action-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "rate-sources"
   ro/row-pk            m.rate-sources/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rate-sources/index
   ro/title             "Rate Sources"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.rate-sources/keys [id name url active currency]
          :ui/keys              [nav-menu rates router]
          :as                   props}]
  {:componentDidMount #(report/start-report! % u.s.rs.rates/Report {})
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
                           :ui/rates                 (comp/get-initial-state u.s.rs.rates/Report)
                           :ui/router                (comp/get-initial-state Router)}))
   :pre-merge         (u.loader/page-merger
                        ::m.rate-sources/id
                        {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                         :ui/router   [Router {}]
                         :ui/rates    [u.s.rs.rates/Report {}]})
   :query             [::m.rate-sources/name
                       ::m.rate-sources/url
                       {::m.rate-sources/currency (comp/get-query u.links/CurrencyLinkForm)}
                       ::m.rate-sources/active
                       ::m.rate-sources/id
                       {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                       {:ui/rates (comp/get-query u.s.rs.rates/Report)}
                       {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-container {:fluid true}
        (ui-segment {}
          (dom/h1 {} (str name))
          (dom/p {} "Url: " (str url))
          (dom/p {} "Active: " (str (boolean active)))
          (dom/p {} "Currency: " (u.links/ui-currency-link currency)))
        (if rates
          (ui-segment {}
            (u.s.rs.rates/ui-report rates))
          (u.debug/load-error props "settings rate-sources rates")))
      (ui-container {}
        (if nav-menu
          (u.menus/ui-nav-menu nav-menu)
          (u.debug/load-error props "settings rate sources nav menu"))
        (if router
          (ui-router router)
          (u.debug/load-error props "settings rate sources router"))))
    (u.debug/load-error props "settings rate sources")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :pre-merge         (u.loader/page-merger nil
                        {:ui/report [Report {}]})
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["rate-sources"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "settings rate sources"))))

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
   :route-segment ["rate-source" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Rate Sources"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Rate Sources"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
