(ns dinsro.ui.currency-accounts-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.sample :as sample]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.forms.add-currency-account :as u.f.add-currency-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard CurrencyAccounts
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.currency-accounts/CurrencyAccounts
    ::ct.fulcro3/initial-state
    (fn []
      {::u.currency-accounts/form (comp/get-initial-state u.f.add-currency-account/AddCurrencyAccountForm)
       ::u.currency-accounts/accounts
       {::u.currency-accounts/accounts (vals sample/account-map)}})}))
