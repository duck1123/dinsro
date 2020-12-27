(ns dinsro.ui.currency-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCurrencyAccountLine
  [_ _]
  (dom/tr
   (dom/td
    "line")))

(def ui-index-currency-account-line (comp/factory IndexCurrencyAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexCurrencyAccounts
  [_ {::keys [accounts]}]
  {:initial-state {::accounts [{}]}
   :query [::accounts]}
  (dom/div
   (dom/table :.table
    (dom/thead
     (dom/tr
      (dom/th "Name")))
    (dom/tbody
     (map ui-index-currency-account-line accounts)))))

(def ui-index-currency-accounts (comp/factory IndexCurrencyAccounts))

(defsc CurrencyAccounts
  [_this {::keys [accounts]}]
  {:initial-state {::accounts {}}
   :query [::accounts]}
  (let [shown? true]
    (bulma/box
     (dom/h1 "currency accounts")
     (when shown?
       (dom/p "form"))
     (dom/hr)
     (ui-index-currency-accounts accounts))))

(def ui-currency-accounts (comp/factory CurrencyAccounts))
