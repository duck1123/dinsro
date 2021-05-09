(ns dinsro.ui.forms.add-currency-rate-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.add-currency-rate :as u.f.add-currency-rate]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AddCurrencyRateForm
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.add-currency-rate/AddCurrencyRateForm
    ::ct.fulcro3/initial-state
    (fn []
      {:component/id ::u.f.add-currency-rate/form})}))
