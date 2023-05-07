(ns dinsro.ui.settings.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.menus :as me]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.settings.rate-sources.accounts :as u.s.rs.accounts]
   [dinsro.ui.settings.rate-sources.rates :as u.s.rs.rates]))

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
  {fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path]
   fo/cancel-route   ["new-rate-source"]
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/id             m.rate-sources/id
   fo/route-prefix   "rate-source"
   fo/title          "New Rate Source"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.s.rs.accounts/SubPage]})

(def ui-router (comp/factory Router))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                         ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/columns           [m.rate-sources/name
                         m.rate-sources/url
                         m.rate-sources/active?]
   ro/route             "rate-sources"
   ro/row-pk            m.rate-sources/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rate-sources/index
   ro/title             "Rate Sources"})

(defsc Show
  [_this {::m.rate-sources/keys [name url active currency id]
          :ui/keys              [rates router]}]
  {:ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/name     ""
                   ::m.rate-sources/id       nil
                   ::m.rate-sources/active   false
                   ::m.rate-sources/currency {}
                   ::m.rate-sources/url      ""
                   :ui/rates                 {}
                   :ui/router                {}}
   :pre-merge     (u.links/page-merger ::m.rate-sources/id
                                       {:ui/router Router
                                        :ui/rates  u.s.rs.rates/Report})
   :query         [::m.rate-sources/name
                   ::m.rate-sources/url
                   {::m.rate-sources/currency (comp/get-query u.links/CurrencyLinkForm)}
                   ::m.rate-sources/active
                   ::m.rate-sources/id
                   {:ui/rates (comp/get-query u.s.rs.rates/Report)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["rate-sources" :id]
   :will-enter    (fn [this props]
                    (report/start-report! this u.s.rs.rates/Report {})
                    (u.links/page-loader ::m.rate-sources/id ::Show this props))}
  (comp/fragment
   (ui-container {:fluid true}
     (dom/div :.ui.segment
       (dom/h1 {} (str name))
       (dom/p {} "Url: " (str url))
       (dom/p {} "Active: " (str (boolean active)))
       (dom/p {} "Currency: " (u.links/ui-currency-link currency)))
     (dom/div :.ui.segment
       (u.s.rs.rates/ui-report rates)))
   (ui-container {}
     (u.links/ui-nav-menu {:menu-items me/settings-rate-sources-menu-items :id id})
     ((comp/factory Router) router))))
