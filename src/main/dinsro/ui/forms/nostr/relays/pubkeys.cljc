(ns dinsro.ui.forms.nostr.relays.pubkeys
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

(form/defsc-form AddForm
  [_this _props]
  {fo/attributes   [m.n.pubkeys/hex]
   fo/id           m.n.pubkeys/id
   fo/route-prefix "new-pubkey"
   fo/title        "Pubkey"})
