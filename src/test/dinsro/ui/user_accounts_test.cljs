(ns dinsro.ui.user-accounts-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 7
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.user-accounts/IndexAccounts
    ::ct.fulcro3/initial-state
    (fn [] {::u.user-accounts/toggle-button {}
            ::u.user-accounts/form          {}
            ::u.user-accounts/accounts
            (map
             (fn [m] (assoc m :button-data {}))
             (vals sample/account-map))
            ::uism/asm-id                   ::u.user-accounts/form-toggle})}))

(ws/defcard UserAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.user-accounts/UserAccounts
    ::ct.fulcro3/initial-state
    (fn []
      (-> (comp/get-initial-state u.user-accounts/UserAccounts)
          (assoc-in
           [::u.user-accounts/accounts ::u.user-accounts/accounts]
           (map (fn [account]
                  (-> account
                      (assoc ::m.accounts/user {::m.users/username "foo"})))
                (vals sample/account-map)))))}))
