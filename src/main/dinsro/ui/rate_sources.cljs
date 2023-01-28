(ns dinsro.ui.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rate-source-accounts :as u.rate-source-accounts]))

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{::m.rate-sources/keys [id]} (comp/props this)]
       (comp/transact! this [(mu.rate-sources/run-query! {::m.rate-sources/id id})])))})

(form/defsc-form NewRateSourceForm
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

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        m.rate-sources/active?]
   ro/controls         {::new-rate-source {:label  "New Source"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewRateSourceForm))}}
   ro/control-layout   {:action-buttons [::new-rate-source]}
   ro/field-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                        ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/route            "rate-sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources Report"})

(report/defsc-report AdminIndexRateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name]
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true})

(defsc ShowRateSource
  [_this {::m.rate-sources/keys [name url]
          :ui/keys              [accounts]}]
  {:route-segment ["rate-sources" :id]
   :query         [::m.rate-sources/name
                   ::m.rate-sources/url
                   ::m.rate-sources/id
                   {:ui/accounts (comp/get-query u.rate-source-accounts/SubPage)}]
   :initial-state {::m.rate-sources/name ""
                   ::m.rate-sources/id   nil
                   ::m.rate-sources/url  ""
                   :ui/accounts          {}}
   :ident         ::m.rate-sources/id
   :will-enter    (partial u.links/page-loader ::m.rate-sources/id ::ShowRateSource)
   :pre-merge     (u.links/page-merger ::m.rate-sources/id {:ui/accounts u.rate-source-accounts/SubPage})}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Rate Source " (str name))
     (dom/p {} "Url: " (str url)))
   (dom/div  :.ui.segment
     (if accounts
       (u.rate-source-accounts/ui-sub-page accounts)
       (dom/p {} "Rate Source accounts not loaded")))))
