(ns dinsro.ui.admin-index-currencies-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-currency :as u.f.admin-create-currency]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AdminIndexCurrencies
  {::wsm/align       {:flex 1}
   ::wsm/card-height 9
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-currencies/AdminIndexCurrencies
    ::ct.fulcro3/initial-state
    (fn []
      {::u.admin-index-currencies/currencies
       [{::m.currencies/id "sats"
         ::m.currencies/link
         [{::m.currencies/id   "sats"
           ::m.currencies/name "Sats"}]}]

       ::u.admin-index-currencies/form
       (comp/get-initial-state
        u.f.admin-create-currency/AdminCreateCurrencyForm)

       ::u.admin-index-currencies/toggle-button
       (comp/get-initial-state
        u.buttons/ShowFormButton
        {:form-button/id u.admin-index-currencies/form-toggle-sm})})}))
