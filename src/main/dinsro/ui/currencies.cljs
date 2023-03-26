(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.currencies.accounts :as u.c.accounts]
   [dinsro.ui.currencies.rate-sources :as u.c.rate-sources]
   [dinsro.ui.links :as u.links]))

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
    u.c.rate-sources/SubPage]})

(def menu-items
  [{:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.currencies.accounts/SubPage"}
   {:key   "rates-sources"
    :name  "Rates Sources"
    :route "dinsro.ui.currencies.rate-sources/SubPage"}])

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.currencies/name
                        m.currencies/code
                        j.currencies/source-count
                        j.currencies/transaction-count
                        j.currencies/rate-count]
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.currencies/name #(u.links/ui-currency-link %3)}
   ro/route            "currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.currencies/index
   ro/title            "Currencies Report"})

(defsc Show
  [_this {::m.currencies/keys [id name]
          :ui/keys            [router]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/name ""
                   ::m.currencies/code ""
                   ::m.currencies/id   nil
                   :ui/router          {}}
   :pre-merge     (u.links/page-merger ::m.currencies/id {:ui/router Router})
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["currency" :id]
   :will-enter    (partial u.links/page-loader ::m.currencies/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str name)))
   (u.links/ui-nav-menu {:menu-items menu-items :id id})
   ((comp/factory Router) router)))
