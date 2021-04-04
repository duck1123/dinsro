(ns dinsro.views.show-account-test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.sample :as sample]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.views.show-account :as v.show-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowAccountPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 15
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.show-account/ShowAccountPage
    ::ct.fulcro3/initial-state
    (fn []
      {::m.accounts/id sample/account-map
       :account-data   (assoc (sample/account-map 1)
                              :user-link-data
                              (rand-nth (vals sample/user-map)))
       :transactions
       {::u.account-transactions/transactions  {:transactions (map sample/transaction-map [1 2])}
        ::u.account-transactions/form          {}
        ::u.account-transactions/toggle-button {}}})}))
