(ns dinsro.ui.forms.nostr.relays.requests
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.requests :as m.n.requests]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.n.requests/id
                    m.n.requests/code]
   fo/cancel-route ["requests"]
   fo/id           m.n.requests/id
   fo/route-prefix "new-request"
   fo/title        "Create Request"})
