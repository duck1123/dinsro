(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.links :as u.links]))

(form/defsc-form NewForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(form/defsc-form NewAdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/route-prefix "new-admin-currency"
   fo/title        "New Currency"})

(def new-button
  {:label  "New"
   :type   :button
   :action #(form/create! % NewForm)})

(report/defsc-report Report
  [_this _props]
  {ro/field-formatters {::m.currencies/name #(u.links/ui-currency-link %3)}
   ro/columns          [m.currencies/name
                        m.currencies/code]
   ro/controls         {::new new-button}
   ro/route            "currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.currencies/index
   ro/title            "Currencies Report"})

(report/defsc-report AdminIndexCurrenciesReport
  [_this _props]
  {ro/columns          [m.currencies/name m.currencies/code]
   ro/controls         {::new {:label  "New Currency"
                               :type   :button
                               :action #(form/create! % NewAdminCurrencyForm)}}
   ro/source-attribute ::j.currencies/index
   ro/title            "Currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true})

(defsc Show
  [_this {::m.currencies/keys [name]
          :ui/keys       [accounts]}]
  {:route-segment ["currency" :id]
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id
                   {:ui/accounts (comp/get-query u.currency-accounts/SubPage)}]
   :initial-state {::m.currencies/name ""
                   ::m.currencies/code ""
                   ::m.currencies/id   nil
                   :ui/accounts        {}}
   :ident         ::m.currencies/id
   :pre-merge     (u.links/page-merger
                   ::m.currencies/id
                   {:ui/accounts u.currency-accounts/SubPage})
   :will-enter    (partial u.links/page-loader ::m.currencies/id ::ShowCurrency)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show Currency " (str name)))
   (u.currency-accounts/ui-sub-page accounts)))
