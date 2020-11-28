(ns dinsro.views.show-user-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.show-user :as v.show-user]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowUserPage
  {::wsm/card-height 26
   ::wsm/card-width 7}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.show-user/ShowUserPage
    ::ct.fulcro3/initial-state
    (fn [] {:user (sample/user-map 1)
            :user-accounts {:index-data {:accounts (map sample/account-map [1])}}
            :user-categories {:index-data {:categories (map sample/category-map [1])}}
            :user-transactions {:index-data {:transactions (map sample/transaction-map [1])}}})
    ::ct.fulcro3/wrap-root? false}))
