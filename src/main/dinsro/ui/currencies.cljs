(ns dinsro.ui.currencies
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
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rates :as u.rates]
   [lambdaisland.glogi :as log]))

(form/defsc-form NewCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   ;; fo/cancel-route ["admin"]
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(form/defsc-form CurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code
                    j.currencies/accounts
                    j.currencies/sources
                    j.currencies/current-rate]
   fo/field-styles {::m.currencies/accounts     :link-list
                    ::m.currencies/sources      :link-list
                    ::m.currencies/transactions :link-list}
   fo/cancel-route ["currencies"]
   fo/route-prefix "currency"
   fo/subforms     {::m.currencies/accounts     {fo/ui u.links/AccountLinkForm}
                    ::m.currencies/current-rate {fo/ui u.rates/RateSubForm}
                    ::m.currencies/sources      {fo/ui u.links/RateSourceLinkForm}
                    ::m.currencies/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/title        "Currency"})

(form/defsc-form AdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin/currency"
   fo/title        "Currency"})

(def new-button
  {:label  "New"
   :type   :button
   :action #(form/create! % NewCurrencyForm)})

(report/defsc-report CurrenciesReport
  [_this _props]
  {ro/column-formatters
   {::m.currencies/name
    (fn [this name {::m.currencies/keys [id]}]
      (dom/a {:onClick #(form/edit! this CurrencyForm id)} name))}
   ro/columns          [m.currencies/name
                        m.currencies/code]
   ro/controls         {::new new-button}
   ro/route            "currencies"
   ro/row-actions      []
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies Report"})

(report/defsc-report AdminIndexCurrenciesReport
  [_this _props]
  {ro/columns          [m.currencies/name m.currencies/code]
   ro/controls         {::new {:label  "New Currency"
                               :type   :button
                               :action #(form/create! % AdminCurrencyForm)}}
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true})

(declare ShowCurrency)

(defn ShowCurrency-pre-merge
  [{:keys [data-tree state-map]}]
  (log/finer :ShowCurrency-pre-merge/starting {:data-tree data-tree :state-map state-map})
  (let [id (::m.currencies/id data-tree)]
    (log/finer :ShowCurrency-pre-merge/parsed {:id id})
    (let [accounts-data (merge
                         (comp/get-initial-state u.currency-accounts/SubPage)
                         (get-in state-map (comp/get-ident u.currency-accounts/SubPage {}))
                         {::m.currencies/id id})]
      (-> data-tree
          (assoc :ui/accounts accounts-data)))))

(defn ShowCurrency-will-enter
  [app {id :id}]
  (let [id    (new-uuid id)
        ident [::m.currencies/id id]
        state (-> (app/current-state app) (get-in ident))]
    (log/finer :ShowCurrency-will-enter/starting {:app app :id id :ident ident})
    (dr/route-deferred
     ident
     (fn []
       (log/finer :ShowCurrency-will-enter/routing
                  {:id       id
                   :state    state
                   :controls (control/component-controls app)})
       (df/load!
        app ident ShowCurrency
        {:marker               :ui/selected-node
         :target               [:ui/selected-node]
         :post-mutation        `dr/target-ready
         :post-mutation-params {:target ident}})))))

(defsc ShowCurrency
  [_this {::m.currencies/keys [name]
          :ui/keys       [accounts]}]
  {:route-segment ["users" :id]
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/accounts (comp/get-query u.currency-accounts/SubPage)}]
   :initial-state {::m.currencies/name ""
                   ::m.currencies/code ""
                   ::m.currencies/id   nil
                   :ui/accounts        {}}
   :ident         ::m.currencies/id
   :will-enter    ShowCurrency-will-enter
   :pre-merge     ShowCurrency-pre-merge}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Currency " (str name)))
   (u.currency-accounts/ui-sub-page accounts)))
