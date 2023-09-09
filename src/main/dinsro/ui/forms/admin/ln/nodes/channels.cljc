(ns dinsro.ui.forms.admin.ln.nodes.channels
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.ln.channels :as m.ln.channels]))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.ln.channels/id
                    m.ln.channels/active
                    m.ln.channels/capacity
                    m.ln.channels/chan-id
                    m.ln.channels/channel-point
                    m.ln.channels/chan-status-flags
                    m.ln.channels/close-address
                    m.ln.channels/commit-fee]
   fo/id           m.ln.channels/id
   fo/route-prefix "new-channel"
   fo/title        "Channels"})
