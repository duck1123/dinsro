(ns dinsro.ui.forms.add-user-account-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.add-user-account :as u.f.add-user-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AddUserAccountForm
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.add-user-account/AddUserAccountForm
    ::ct.fulcro3/initial-state
    (fn []
      {::u.f.add-user-account/currency      {:label "currency"      :value "7"}
       ::u.f.add-user-account/initial-value "1"
       ::u.f.add-user-account/name          "Savings Account"})}))
