(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexAccountLine
  [_this {::m.accounts/keys [currency id initial-value link user]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/link          {}
                   ::m.accounts/currency      {}
                   ::m.accounts/id            nil
                   ::m.accounts/initial-value 0
                   ::m.accounts/user          {}}
   :query         [{::m.accounts/link (comp/get-query u.links/AccountLink)}
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.accounts/id
                   ::m.accounts/initial-value
                   {::m.accounts/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td (u.links/ui-account-link (first link)))
    (dom/td (u.links/ui-user-link (first user)))
    (dom/td (u.links/ui-currency-link (first currency)))
    (dom/td initial-value)
    (dom/td (u.buttons/ui-delete-account-button {::m.accounts/id id}))))

(def ui-index-account-line (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {::keys [accounts]}]
  {:initial-state {::accounts []}
   :query         [{::accounts (comp/get-query IndexAccountLine)}]}
  (if (seq accounts)
    (dom/table :.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:name]))
          (dom/th (tr [:user-label]))
          (dom/th (tr [:currency-label]))
          (dom/th (tr [:initial-value-label]))
          (dom/th (tr [:buttons]))))
      (dom/tbody {}
        (map ui-index-account-line accounts)))
    (dom/div (tr [:no-accounts]))))

(def ui-index-accounts (comp/factory IndexAccounts))

(defsc UserAccounts
  [this {::keys [accounts form toggle-button]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar UserAccounts}))
   :ident         (fn [_] [:component/id ::UserAccounts])
   :initial-state {::accounts      {}
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query         [{::accounts (comp/get-query IndexAccounts)}
                   {::form (comp/get-query u.f.add-user-account/AddUserAccountForm)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 {}
       (tr [:accounts])
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-user-account/ui-form form))
     (dom/hr)
     (ui-index-accounts accounts))))

(def ui-user-accounts
  (comp/factory UserAccounts))
