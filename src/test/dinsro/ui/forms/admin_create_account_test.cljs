(ns dinsro.ui.forms.admin-create-account-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminCreateAccountForm
  {::wsm/align       {:flex 1}
   ::wsm/card-height 7
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.admin-create-account/AdminCreateAccountForm
    ::ct.fulcro3/initial-state
    (fn []
      {::u.f.admin-create-account/currency      {}
       ::u.f.admin-create-account/initial-value "0"
       ::u.f.admin-create-account/name          ""
       ::u.f.admin-create-account/user          {}})}))
