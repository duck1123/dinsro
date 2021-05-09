(ns dinsro.ui.forms.add-user-account-test
  (:require
   [clojure.spec.alpha]
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [dinsro.ui.inputs :as u.inputs]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AddUserAccountForm
  {::wsm/align       {:flex 1}
   ::wsm/card-height 8
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.add-user-account/AddUserAccountForm
    ::ct.fulcro3/initial-state
    (fn []
      {::u.f.add-user-account/submit        (comp/get-initial-state
                                             u.inputs/PrimaryButton)
       ::u.f.add-user-account/currency      (comp/get-initial-state
                                             u.inputs/CurrencySelector
                                             {:label "currency" :value "7"})
       ::u.f.add-user-account/initial-value "1"
       ::u.f.add-user-account/name          "Savings Account"})}))
