(ns dinsro.ui.links-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AccountLink
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.links/AccountLink
    ::ct.fulcro3/initial-state
    (fn [] {:account      {1 {:account/id   1
                              :account/name "bar"}}
            :account/id   1
            :account/name "Foo"})}))

(ws/defcard CurrencyLink
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.links/CurrencyLink
    ::ct.fulcro3/initial-state
    (fn [] {:currency/id   1
            :currency/name "Currency"})}))

(ws/defcard UserLink
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.links/UserLink
    ::ct.fulcro3/initial-state
    (fn []
      (rand-nth (vals sample/user-map)))}))
