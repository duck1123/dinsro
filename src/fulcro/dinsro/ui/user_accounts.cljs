(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [dinsro.ui.index-accounts :as u.index-accounts]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountLine
  [_this {::m.accounts/keys [name user-id currency-id initial-value]
          :keys [button-data]}]
  {:ident ::m.accounts/id
   :query [::m.accounts/id
           ::m.accounts/name
           ::m.accounts/currency-id
           ::m.accounts/user-id
           ::m.accounts/initial-value
           {:button-data (comp/get-query u.buttons/DeleteButton)}]}
  (dom/tr
   (dom/td name)
   (dom/td user-id)
   (dom/td currency-id)
   (dom/td initial-value)
   (dom/td
    (u.buttons/ui-delete-button button-data))))

(def ui-index-account-line (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {:keys [accounts]}]
  {:query [{:accounts (comp/get-query IndexAccountLine)}]
   :initial-state {:accounts []}}
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
  {:query [{::accounts      (comp/get-query u.index-accounts/IndexAccounts)}
           {::form          (comp/get-query u.f.add-user-account/AddUserAccountForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]
   :initial-state {::accounts      {}
                   ::form          {}
                   ::toggle-button {}}}
  (let [shown? false]
    (bulma/box
     (dom/h2
      (tr [:accounts])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-user-account/ui-form form))
     (ui-index-accounts accounts))))

(def ui-user-accounts
  (comp/factory UserAccounts))
