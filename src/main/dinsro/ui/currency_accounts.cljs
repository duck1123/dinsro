(ns dinsro.ui.currency-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-currency-account :as u.f.add-currency-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCurrencyAccountLine
  [_ {::m.accounts/keys [link]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/link {}}
   :query         [::m.accounts/id
                   {::m.accounts/link (comp/get-query u.links/AccountLink)}]}
  (dom/tr {}
    (dom/td
     {}
     (u.links/ui-account-link link))))

(def ui-index-currency-account-line (comp/factory IndexCurrencyAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexCurrencyAccounts
  [_ _]
  {:initial-state {::accounts []}
   :query         [::accounts]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (merge {:bar :baz} current-normalized data-tree))})

(def ui-index-currency-accounts (comp/factory IndexCurrencyAccounts))

(defsc CurrencyAccounts
  [this {::keys              [form toggle-button]
         ::m.currencies/keys [accounts]}]
  {:componentDidMount
   #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CurrencyAccounts})
   :ident         ::m.currencies/id
   :initial-state (fn [_]
                    {::m.currencies/id       nil
                     ::m.currencies/accounts []
                     ::form                  {}
                     ::toggle-button         {:form-button/id form-toggle-sm}})
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {::form          {}
                                       ::toggle-button {:form-button/id form-toggle-sm}}
                          merged-data (merge defaults current-normalized data-tree)]
                      merged-data))
   :query         [::m.currencies/id
                   {::m.currencies/accounts (comp/get-query IndexCurrencyAccountLine)}
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
     (dom/div {}
       (if (empty? accounts)
         (dom/div {} (tr [:no-currencies]))
         (dom/table :.ui.table
           (dom/thead {}
             (dom/tr {}
               (dom/th "Name")))
           (dom/tbody {}
             (map ui-index-currency-account-line accounts))))))))

(def ui-currency-accounts (comp/factory CurrencyAccounts))
