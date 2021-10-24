(ns dinsro.ui.show-user-test
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-user :as u.show-user]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard ShowUser
  {::wsm/card-height 7
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-user/ShowUser
    ::ct.fulcro3/initial-state
    (fn []
      {::m.users/id m.users/default-username})}))

(ws/defcard ShowUserPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 26
   ::wsm/card-width  7}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-user/ShowUserPage
    ::ct.fulcro3/initial-state
    (fn []
      {::u.show-user/user (sample/user-map 1)

       ::u.show-user/user-accounts
       {::u.user-accounts/accounts
        {::u.user-accounts/accounts (map sample/account-map [1])}}

       ::u.show-user/user-categories
       {:index-data
        {:categories (map sample/category-map [1])}}

       ::u.show-user/user-transactions
       {::u.user-transactions/form          {}
        ::u.user-transactions/toggle-button {}
        ::u.user-transactions/transactions
        {::u.user-transactions/transactions (map sample/transaction-map [1])}}})}))
