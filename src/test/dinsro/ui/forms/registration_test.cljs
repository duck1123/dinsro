(ns dinsro.ui.forms.registration-test
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.ui.forms.registration :as u.f.registration]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard RegistrationForm
  {::wsm/card-width  3
   ::wsm/card-height 10}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/initial-state
    (fn []
      {::u.f.registration/username         (ds/gen-key ::m.users/id)
       ::u.f.registration/password         (ds/gen-key ::m.users/password)
       ::u.f.registration/confirm-password (ds/gen-key ::m.users/password)})
    ::ct.fulcro3/root u.f.registration/RegistrationForm}))
