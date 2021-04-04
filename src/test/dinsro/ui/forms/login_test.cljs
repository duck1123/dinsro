(ns dinsro.ui.forms.login-test
  (:require
   [dinsro.ui.forms.login :as u.f.login]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard LoginForm
  {::wsm/card-width  2
   ::wsm/card-height 9}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.login/LoginForm}))
