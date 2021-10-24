(ns dinsro.ui.index-rates-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.index-rates :as u.index-rates]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexRatesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-rates/IndexRatesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-rates/form          {}
       ::u.index-rates/rates
       {::u.index-rates/items (vals sample/rate-map)}
       ::u.index-rates/toggle-button {}})}))
