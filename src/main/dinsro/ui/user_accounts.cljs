(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

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

(defsc UserAccounts
  [_this {::m.users/keys [accounts id]}]
  {:ident              ::m.users/id
   :initial-state      (fn [_]
                         {::m.users/accounts []
                          ::m.users/id       nil})
   :query              [::m.users/id
                        {::m.users/accounts (comp/get-query IndexAccountLine)}]}
  (when id
    (bulma/box
     (dom/h2 {}
       (tr [:accounts]))
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
       (dom/div (tr [:no-accounts]))))))

(def ui-user-accounts
  (comp/factory UserAccounts))
