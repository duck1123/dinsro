(ns dinsro.ui.registration-test
  (:require
   [dinsro.ui.registration :as u.registration]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard RegistrationPage
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.registration/RegistrationPage
    ::ct.fulcro3/initial-state
    (fn []
      {::u.registration/allow-registration true
       ::u.registration/form
       {:username         ""
        :password         ""
        :confirm-password ""}})}))
