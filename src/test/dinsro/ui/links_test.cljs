(ns dinsro.ui.links-test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
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
    (fn [] {::m.accounts/id   "dsfdds"
            ::m.accounts/name "Foo"})}))

(ws/defcard CurrencyLink
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.links/CurrencyLink
    ::ct.fulcro3/initial-state
    (fn [] {::m.currencies/id   "currency"
            ::m.currencies/name "Currency"})}))

(ws/defcard UserLink
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.links/UserLink
    ::ct.fulcro3/initial-state
    (fn []
      {::m.users/id "admin"})}))
