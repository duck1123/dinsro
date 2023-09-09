(ns dinsro.ui.forms.nostr.events.event-tags
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]))

(form/defsc-form AddForm
  [_this _props]
  {fo/attributes   [m.n.event-tags/index]
   fo/id           m.n.event-tags/id
   fo/route-prefix "new-tag"
   fo/title        "Tags"})
