(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.currencies.accounts :as u.c.accounts]
   [dinsro.ui.currencies.rate-sources :as u.c.rate-sources]
   [dinsro.ui.currencies.rates :as u.c.rates]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

;; [[../actions/currencies.clj]]
;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]

(def model-key ::m.currencies/id)

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
   ro/route             "currencies"
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.currencies/id mu.currencies/delete!)]
   ro/row-pk            m.currencies/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.currencies/index
   ro/title             "Currencies Report"})

(defsc Show
  [_this {::m.currencies/keys [name]
          :ui/keys            [nav-menu router]}]
  {:ident         ::m.currencies/id
   :initial-state (fn [props]
                    (let [id (::m.currencies/id props)]
                      {::m.currencies/name ""
                       ::m.currencies/code ""
                       ::m.currencies/id   nil
                       :ui/nav-menu        (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :currencies
                                                                                    :id            id})
                       :ui/router          (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger
                   ::m.currencies/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :currencies}]
                    :ui/router   [Router {}]})
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["currency" :id]
   :will-enter    (partial u.loader/page-loader ::m.currencies/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str name)))
   (u.menus/ui-nav-menu nav-menu)
   (ui-router router)))
