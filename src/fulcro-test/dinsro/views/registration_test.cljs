(ns dinsro.views.registration-test
  (:require
   [dinsro.views.registration :as v.registration]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard RegistrationPage
  {::wsm/card-height 14
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.registration/RegistrationPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.registration/allow-registration true
       ::v.registration/form
       {:name ""
        :email ""
        :password ""
        :confirm-password ""}})}))
