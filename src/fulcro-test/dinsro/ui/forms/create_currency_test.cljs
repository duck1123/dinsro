(ns dinsro.ui.forms.create-currency-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard CreateCurrencyForm
  {::wsm/card-height 5
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.create-currency/CreateCurrencyForm
    ::ct.fulcro3/initial-state
    (fn []
      {::u.f.create-currency/name ""})}))
