(ns dinsro.ui.currency-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-currency-account :as u.f.add-currency-account]
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
  {:initial-state {::accounts []}
   :query         [::accounts]}
  (dom/div
   (if (empty? accounts)
     (dom/div (tr [:no-currencies]))
     (dom/table
      :.table
      (dom/thead
       (dom/tr
        (dom/th "Name")))
      (dom/tbody
       (map ui-index-currency-account-line accounts))))))

(def ui-index-currency-accounts (comp/factory IndexCurrencyAccounts))

(defsc CurrencyAccounts
  [this {::keys [accounts form toggle-button]}]
  {:componentDidMount
   #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CurrencyAccounts})
   :ident         (fn [] [:component/id ::CurrencyAccounts])
   :initial-state {::accounts      {}
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query         [{::accounts (comp/get-query IndexCurrencyAccounts)}
                   {::form (comp/get-query u.f.add-currency-account/AddCurrencyAccountForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h1
      (tr [:accounts])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-currency-account/ui-form form))
     (dom/hr)
     (ui-index-currency-accounts accounts))))

(def ui-currency-accounts (comp/factory CurrencyAccounts))
