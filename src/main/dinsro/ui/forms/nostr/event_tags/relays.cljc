(ns dinsro.ui.forms.nostr.event-tags.relays
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.relays :as m.n.relays]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.n.relays/id]
   fo/cancel-route  ["relays"]
   fo/id            m.n.relays/id
   fo/route-prefix  "create-relay"
   fo/title         "Relay"})
