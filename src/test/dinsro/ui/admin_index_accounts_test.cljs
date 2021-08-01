(ns dinsro.ui.admin-index-accounts-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AdminIndexAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 8
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-accounts/AdminIndexAccounts
    ::ct.fulcro3/initial-state
    (fn []
      (let [account-id (new-uuid)]
        {:component/id ::AdminIndexAccounts

         ::u.admin-index-accounts/accounts
         [{::m.accounts/currency      [{::m.currencies/id   "foo"
                                        ::m.currencies/name "foo"}]
           ::m.accounts/link          [{::m.accounts/id   account-id
                                        ::m.accounts/name "Account Name"}]
           ::m.accounts/id            account-id
           ::m.accounts/initial-value 42
           ::m.accounts/user          [{::m.users/id "userid"}]}]

         ::u.admin-index-accounts/form
         (comp/get-initial-state u.f.admin-create-account/AdminCreateAccountForm)

         ::u.admin-index-accounts/toggle-button
         (comp/get-initial-state u.buttons/ShowFormButton)}))}))
