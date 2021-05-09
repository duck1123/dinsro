(ns dinsro.views.index-currencies-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [dinsro.ui.user-currencies :as u.user-currencies]
   [dinsro.views.index-currencies :as v.index-currencies]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexCurrenciesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-currencies/IndexCurrenciesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.index-currencies/currencies
       {::u.user-currencies/currencies
        {::u.index-currencies/currencies
         (map sample/currency-map ["sats" "usd"])}}
       ::v.index-currencies/form          {}
       ::v.index-currencies/toggle-button {}})}))
