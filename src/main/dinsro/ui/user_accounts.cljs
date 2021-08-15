(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
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
    (dom/td (u.links/ui-account-link link))
    (dom/td (u.links/ui-user-link user))
    (dom/td (u.links/ui-currency-link currency))
    (dom/td initial-value)
    (dom/td (u.buttons/ui-delete-account-button {::m.accounts/id id}))))

(def ui-index-account-line (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this _]
  {:initial-state {}
   :query         []})

(def ui-index-accounts (comp/factory IndexAccounts))

(defsc UserAccounts
  [this {::m.users/keys [accounts id]
         ::keys         [form toggle-button]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar
                                                         (uism/with-actor-class [::m.users/id :none]
                                                           UserAccounts)}))
   :ident              ::m.users/id
   :initial-state      (fn [_]
                         {::m.users/accounts []
                          ::m.users/id       nil
                          ::form             (comp/get-initial-state u.f.add-user-account/AddUserAccountForm)
                          ::toggle-button    {:form-button/id form-toggle-sm}})
   :pre-merge          (fn [{:keys [current-normalized data-tree]}]
                         (let [defaults    {::form          (comp/get-initial-state u.f.add-user-account/AddUserAccountForm)
                                            ::toggle-button {:form-button/id form-toggle-sm}}
                               merged-data (merge current-normalized data-tree defaults)]
                           merged-data))
   :query              [::m.users/id
                        {::m.users/accounts (comp/get-query IndexAccountLine)}
                        {::form (comp/get-query u.f.add-user-account/AddUserAccountForm)}
                        {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                        [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (when id
      (bulma/box
       (dom/h2 {}
         (tr [:accounts])
         (when toggle-button (u.buttons/ui-show-form-button toggle-button)))
       (when shown?
         (when form (u.f.add-user-account/ui-form form)))
       (dom/hr)
       (if (seq accounts)
         (dom/table :.ui.table
           (dom/thead {}
             (dom/tr {}
               (dom/th (tr [:name]))
               (dom/th (tr [:user-label]))
               (dom/th (tr [:currency-label]))
               (dom/th (tr [:initial-value-label]))
               (dom/th (tr [:buttons]))))
           (dom/tbody {}
             (map ui-index-account-line accounts)))
         (dom/div (tr [:no-accounts])))))))

(def ui-user-accounts
  (comp/factory UserAccounts))
