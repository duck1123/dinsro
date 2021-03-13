(ns dinsro.views.show-user-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [dinsro.views.show-user :as v.show-user]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowUserPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 26
   ::wsm/card-width  7}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.show-user/ShowUserPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.show-user/user (sample/user-map 1)

       ::v.show-user/user-accounts
       {::u.user-accounts/accounts
        {::u.user-accounts/accounts (map sample/account-map [1])}}

       ::v.show-user/user-categories
       {:index-data
        {:categories (map sample/category-map [1])}}

       ::v.show-user/user-transactions
       {::u.user-transactions/form          {}
        ::u.user-transactions/toggle-button {}
        ::u.user-transactions/transactions
        {::u.user-transactions/transactions (map sample/transaction-map [1])}}})}))
