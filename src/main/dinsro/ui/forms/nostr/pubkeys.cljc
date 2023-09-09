(ns dinsro.ui.forms.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

(form/defsc-form CreateForm
  [_this _props]
  {fo/attributes   [m.n.pubkeys/hex]
   fo/cancel-route ["pubkeys"]
   fo/id           m.n.pubkeys/id
   fo/route-prefix "create-pubkey"
   fo/title        "Create A Pubkey"})
