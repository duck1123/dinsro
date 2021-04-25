(ns dinsro.ui.index-accounts-test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.sample :as sample]
   [dinsro.ui.index-accounts :as u.index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexAccounts
  {::wsm/card-height 6
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root       u.index-accounts/IndexAccounts
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-accounts/accounts
       (map
        (fn [account]
          (-> account
              (assoc ::m.accounts/user [{::m.users/name "foo"
                                         ::m.users/id   1}])
              (assoc ::m.accounts/currency [{::m.currencies/name "bar"
                                             ::m.currencies/id   1}])))
        (vals sample/account-map))})
    ::ct.fulcro3/wrap-root? false}))
