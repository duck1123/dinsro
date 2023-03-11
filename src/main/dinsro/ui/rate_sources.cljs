(ns dinsro.ui.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rate-sources.accounts :as u.rs.accounts]
   [dinsro.ui.rate-sources.rates :as u.rs.rates]))

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{::m.rate-sources/keys [id]} (comp/props this)]
       (comp/transact! this [(mu.rate-sources/run-query! {::m.rate-sources/id id})])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/id             m.rate-sources/id
   fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path]
   fo/cancel-route   ["new-rate-source"]
   fo/route-prefix   "rate-source"
   fo/title          "New Rate Source"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.rs.accounts/SubPage
    u.rs.rates/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.rate-sources.accounts/SubPage"}
   {:key   "rates"
    :name  "Rates"
    :route "dinsro.ui.rate-sources.rates/SubPage"}])

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        m.rate-sources/active?]
   ro/controls         {::new-rate-source {:label  "New Source"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewForm))}}
   ro/control-layout   {:action-buttons [::new-rate-source]}
   ro/field-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                        ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/route            "rate-sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources Report"})

(defsc Show
  [_this {::m.rate-sources/keys [name url active currency id]
          ::j.rate-sources/keys [rate-count]
          :ui/keys              [router]
          :as                   props}]
  {:route-segment ["rate-sources" :id]
   :query         [::m.rate-sources/name
                   ::m.rate-sources/url
                   {::m.rate-sources/currency (comp/get-query u.links/CurrencyLinkForm)}
                   ::m.rate-sources/active
                   ::m.rate-sources/id
                   ::j.rate-sources/rate-count
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.rate-sources/name       ""
                   ::m.rate-sources/id         nil
                   ::m.rate-sources/active     false
                   ::m.rate-sources/currency   {}
                   ::m.rate-sources/url        ""
                   ::j.rate-sources/rate-count []
                   :ui/router                  {}}
   :ident         ::m.rate-sources/id
   :pre-merge     (u.links/page-merger ::m.rate-sources/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.rate-sources/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/a {:href "/rate-sources"} "Rate Sources"))
   (dom/div :.ui.segment
     (dom/h1 {} (str name))
     (dom/p {} "Url: " (str url))
     (dom/p {} "Active: " (str active))
     (dom/p {} "Currency: " (u.links/ui-currency-link currency))
     (dom/p {} "Rate Count: " (str rate-count)))
   (u.links/ui-nav-menu {:menu-items menu-items :id id})
   (if router
     (ui-router router)
     (dom/div :.ui.segment
       (dom/h3 {} "Router not loaded")
       (u.links/ui-props-logger props)))))
