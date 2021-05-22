(ns dinsro.ui.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc ShowAccount
  [_this {::m.accounts/keys [account currency id name user]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/account  {}
                   ::m.accounts/currency {}
                   ::m.accounts/name     ""
                   ::m.accounts/user     {}}
   :query         [{::m.accounts/account (comp/get-query u.links/AccountLink)}
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.accounts/id
                   ::m.accounts/name
                   {::m.accounts/user (comp/get-query u.links/UserLink)}
                   :user-link-data]}
  (dom/div {}
    (dom/h3 name)
    (dom/p (u.links/ui-account-link account))
    (dom/p
     (tr [:user-label])
     (u.links/ui-user-link user))
    (dom/p
     (tr [:currency-label])
     (u.links/ui-currency-link currency))
    (u.buttons/ui-delete-account-button {::m.accounts/id id})))

(def ui-show-account (comp/factory ShowAccount))

(defsc ShowAccountFull
  [_ {::m.accounts/keys [link currency id name account-transactions user]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/account-transactions {}
                   ::m.accounts/link                 {}
                   ::m.accounts/currency             {}
                   ::m.accounts/id                   nil
                   ::m.accounts/name                 ""
                   ::m.accounts/user                 {}}
   :query         [{::m.accounts/link (comp/get-query u.links/AccountLink)}
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.accounts/id
                   ::m.accounts/name
                   {::m.accounts/user (comp/get-query u.links/UserLink)}
                   :user-link-data
                   {::m.accounts/account-transactions (comp/get-query u.account-transactions/AccountTransactions)}]}
  (if id
    (bulma/page
     (bulma/box
      (dom/div {}
        (dom/h3 name)
        (dom/p (when link (u.links/ui-account-link link)))
        (dom/p
         (tr [:user-label])
         (when user (u.links/ui-user-link user)))
        (dom/p
         (tr [:currency-label])
         (when currency (u.links/ui-currency-link currency)))
        (u.buttons/ui-delete-account-button {::m.accounts/id id})))
     (when account-transactions
       (u.account-transactions/ui-account-transactions account-transactions)))
    (dom/p "no account")))

(def ui-show-account-full (comp/factory ShowAccountFull))
