(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.currencies.accounts :as u.c.accounts]
   [dinsro.ui.currencies.rate-sources :as u.c.rate-sources]
   [dinsro.ui.currencies.rates :as u.c.rates]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.currencies :as u.f.currencies]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

;; [[../actions/currencies.clj]]
;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]
;; [[../ui/forms/currencies.cljc]]

(def index-page-id :currencies)
(def model-key ::m.currencies/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :currencies-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.currencies/delete!))

(def override-form false)

(defsc ShowCurrency
  [_this {::m.currencies/keys [code name] :as _props}]
  {}
  (dom/div {}
    (dom/p {} "Name: " name)
    (dom/p {} "Code: " code)))

(defsc ShowCurrencyPage
  [_this {::m.currencies/keys [id name]}]
  {:ident         (fn [] [::m.currencies/id (new-uuid id)])
   :query         [::m.currencies/id
                   ::m.currencies/name]
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/name ""}
   :route-segment ["currencies" :id]
   :will-enter
   (fn [app {id :id}]
     (let [ident [::m.currencies/id (new-uuid id)]]
       (if (-> (app/current-state app) (get-in ident) ::m.currencies/name)
         (dr/route-immediate ident)
         (let [options  {:post-mutation        `dr/target-ready
                         :post-mutation-params {:target ident}}
               callback #(df/load app ident ShowCurrencyPage options)]
           (dr/route-deferred ident callback)))))}
  (dom/div {}
    (dom/h1 {} "Show Currency")
    (dom/p {} (str "Name: " name))))

(def ui-show-currency (comp/factory ShowCurrency))

(def new-button
  (u.buttons/form-create-button "New" u.f.currencies/NewForm))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.c.accounts/SubPage
    u.c.rate-sources/SubPage
    u.c.rates/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.c.rate-sources/index-page-id
    u.c.accounts/index-page-id
    u.c.rates/index-page-id]})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.currencies/name #(u.links/ui-currency-link %3)}
   ro/columns           [m.currencies/name
                         m.currencies/code
                         j.currencies/source-count
                         j.currencies/transaction-count
                         j.currencies/rate-count]
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.currencies/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.currencies/index
   ro/title             "Currencies Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.currencies/keys [id name]
          :ui/keys            [nav-menu router]
          :as                 props}]
  {:ident         ::m.currencies/id
   :initial-state (fn [props]
                    (let [id (::m.currencies/id props)]
                      {::m.currencies/name ""
                       ::m.currencies/code ""
                       ::m.currencies/id   nil
                       :ui/nav-menu        (comp/get-initial-state u.menus/NavMenu
                                             {::m.navbars/id show-page-id
                                              :id            id})
                       :ui/router          (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str name)))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))
    (u.debug/load-error props "Show currency record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["currencies"]
   :will-enter        (u.loader/page-loader index-page-id)}
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
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Currencies"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Currency"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
