(ns dinsro.ui.admin-index-accounts-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(def accounts (map sample/account-map [1 2]))

(ws/defcard AdminIndexAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-accounts/AdminIndexAccounts
    ::ct.fulcro3/initial-state
    (fn []
      {::m.currencies/id                      sample/currency-map
       :component/id                          {}
       ::u.admin-index-accounts/accounts
       [{::m.accounts/currency
         {::m.currencies/id   1
          ::m.currencies/name "foo"}
         ::m.accounts/id            1
         ::m.accounts/initial-value 42
         ::m.users/name             "x"
         ::m.accounts/user
         {::m.users/id   1
          ::m.users/name "Bob"}}]
       ::u.admin-index-accounts/form          (comp/get-initial-state u.f.admin-create-account/AdminCreateAccountForm)
       ::u.admin-index-accounts/toggle-button (comp/get-initial-state u.buttons/ShowFormButton)})}))
