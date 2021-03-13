(ns dinsro.ui.forms.create-account-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.create-account :as u.f.create-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard CreateAccountForm
  {::wsm/align       {:flex 1}
   ::wsm/card-height 4
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.create-account/CreateAccountForm
    ::ct.fulcro3/initial-state
    (fn []
      {::u.f.create-account/name ""})}))
