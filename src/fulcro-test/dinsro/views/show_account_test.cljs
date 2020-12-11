(ns dinsro.views.show-account-test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.sample :as sample]
   [dinsro.views.show-account :as v.show-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowAccountPage
  {::wsm/align {:flex 1}
   ::wsm/card-height 14
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.show-account/ShowAccountPage
    ::ct.fulcro3/initial-state
    (fn [] {::m.accounts/id sample/account-map
            :account-data (sample/account-map 1)
            :transactions {:transaction-data {:transactions (map sample/transaction-map [1 2])}
                           :form-data {}
                           :button-data {}}})
    ::ct.fulcro3/wrap-root? false}))
