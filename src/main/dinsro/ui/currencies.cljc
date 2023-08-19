(ns dinsro.ui.currencies
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
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.currencies.accounts :as u.c.accounts]
   [dinsro.ui.currencies.rate-sources :as u.c.rate-sources]
   [dinsro.ui.currencies.rates :as u.c.rates]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../actions/currencies.clj]]
;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]

(def index-page-key :currencies)
(def model-key ::m.currencies/id)
(def show-menu-id :currencies)
(def show-page-key :currencies-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.currencies/delete!))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/id           m.currencies/id
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(def new-button
  {:label  "New"
   :type   :button
   :action #(form/create! % NewForm)})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.c.accounts/SubPage
    u.c.rate-sources/SubPage
    u.c.rates/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :root
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.c.rate-sources/index-page-key
    u.c.accounts/index-page-key
    u.c.rates/index-page-key]})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.currencies/name #(u.links/ui-currency-link %3)}
   ro/columns           [m.currencies/name
                         m.currencies/code
                         j.currencies/source-count
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
                                             {::m.navbars/id show-menu-id
                                              :id            id})
                       :ui/router          (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]
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
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["currencies"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.currencies/keys [id]
          ::m.navlinks/keys   [target]
          :as                 props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.currencies/id   nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.currencies/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show currency page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Currencies"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :user})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Show Currency"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :user})
