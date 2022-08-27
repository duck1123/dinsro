(ns dinsro.ui.rate-sources
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rate-source-accounts :as u.rate-source-accounts]
   [dinsro.ui.rates :as u.rates]
   [lambdaisland.glogi :as log]))

(form/defsc-form RateSubform
  [_this _props]
  {fo/id           m.rates/id
   fo/route-prefix "rate-source-rate"
   fo/attributes   [m.rates/rate
                    m.rates/date]})

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{::m.rate-sources/keys [id]} (comp/props this)]
       (comp/transact! this [(mu.rate-sources/run-query! {::m.rate-sources/id id})])))})

(form/defsc-form RateSourceForm
  [_this _props]
  {fo/id             m.rate-sources/id
   fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/subforms       {::m.rate-sources/currency     {fo/ui u.links/CurrencyLinkForm}
                      ::m.rate-sources/current-rate {fo/ui u.rates/RateSubForm}
                      ::m.rate-sources/rates        {fo/ui u.rates/RateSubForm}}
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path
                      j.rate-sources/rates]
   fo/cancel-route   ["rate-sources"]
   fo/field-styles   {::m.rate-sources/rates :rate-chart}
   fo/route-prefix   "rate-source"
   fo/title          "Rate Source"})

(report/defsc-report RateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        m.rate-sources/active?]
   ro/controls         {::new-rate-source {:label  "New Source"
                                           :type   :button
                                           :action (fn [this] (form/create! this RateSourceForm))}}
   ro/control-layout   {:action-buttons [::new-rate-source]}
   ro/field-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                        ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/route            "rate-sources"
   ro/row-actions      []
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rate-sources/index
   ro/title            "Rate Sources Report"})

(report/defsc-report AdminIndexRateSourcesReport
  [_this _props]
  {ro/columns          [m.rate-sources/name]
   ro/source-attribute ::m.rate-sources/index
   ro/title            "Rate Sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true})

(declare ShowRateSource)

(defn ShowRateSource-will-enter
  [app {id :id}]
  (let [id    (new-uuid id)
        ident [::m.rate-sources/id id]
        state (-> (app/current-state app) (get-in ident))]
    (log/finer :ShowRateSource-will-enter/starting {:app app :id id :ident ident})
    (dr/route-deferred
     ident
     (fn []
       (log/finer :ShowRateSource-will-enter/routing
                  {:id       id
                   :state    state
                   :controls (control/component-controls app)})
       (df/load!
        app ident ShowRateSource
        {:marker               :ui/selected-node
         :target               [:ui/selected-node]
         :post-mutation        `dr/target-ready
         :post-mutation-params {:target ident}})))))

(defn ShowRateSource-pre-merge
  [ctx]
  (u.links/merge-pages
   ctx
   ::m.rate-sources/id
   {:ui/accounts u.rate-source-accounts/SubPage}))

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
   :will-enter    ShowRateSource-will-enter
   :pre-merge     ShowRateSource-pre-merge}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Rate Source " (str name))
     (dom/p {} "Url: " (str url)))
   (dom/div  :.ui.segment
     (if accounts
       (u.rate-source-accounts/ui-sub-page accounts)
       (dom/p {} "Rate Source accounts not loaded")))))
