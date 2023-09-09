(ns dinsro.ui.forms.nostr.events
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.events :as m.n.events]))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/id           m.n.events/id
   fo/route-prefix "new-event"
   fo/title        "Event"})
