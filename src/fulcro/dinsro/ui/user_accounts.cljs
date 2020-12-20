(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountLine
  [_this {::m.accounts/keys [currency initial-value name user]
          ::keys [button-data]}]
  {:ident ::m.accounts/id
   :initial-state {::button-data              {}
                   ::m.accounts/currency      {}
                   ::m.accounts/id            0
                   ::m.accounts/initial-value 0
                   ::m.accounts/name          ""
                   ::m.accounts/user          {}}
   :query [{::button-data             (comp/get-query u.buttons/DeleteButton)}
           {::m.accounts/currency     (comp/get-query u.links/CurrencyLink)}
           ::m.accounts/id
           ::m.accounts/initial-value
           ::m.accounts/name
           {::m.accounts/user         (comp/get-query u.links/UserLink)}]}
  (dom/tr
   (dom/td name)
   (dom/td (u.links/ui-user-link user))
   (dom/td (u.links/ui-currency-link currency))
   (dom/td initial-value)
   (dom/td (u.buttons/ui-delete-button button-data))))

(def ui-index-account-line (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {::keys [accounts]}]
  {:initial-state {::accounts []}
   :query [{::accounts (comp/get-query IndexAccountLine)}]}
  (if (seq accounts)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:name]))
       (dom/th (tr [:user-label]))
       (dom/th (tr [:currency-label]))
       (dom/th (tr [:initial-value-label]))
       (dom/th (tr [:buttons]))))
     (dom/tbody
      (map ui-index-account-line accounts)))
    (dom/div (tr [:no-accounts]))))

(def ui-index-accounts (comp/factory IndexAccounts))

(defsc UserAccounts
  [_this {::keys [accounts form toggle-button]}]
  {:initial-state {::accounts      {}
                   ::form          {}
                   ::toggle-button {}}
   :query [{::accounts      (comp/get-query IndexAccounts)}
           {::form          (comp/get-query u.f.add-user-account/AddUserAccountForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (let [shown? false]
    (bulma/box
     (dom/h2
      (tr [:accounts])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-user-account/ui-form form))
     (dom/hr)
     (ui-index-accounts accounts))))

(def ui-user-accounts
  (comp/factory UserAccounts))
