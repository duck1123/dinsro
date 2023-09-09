(ns dinsro.ui.forms.admin.nostr.requests
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.requests :as m.n.requests]))

(form/defsc-form EditForm [_this _props]
  {fo/attributes    [m.n.requests/id]
   fo/cancel-route  ["requests"]
   ;; fo/field-styles  {::m.transactions/account :pick-one}
   ;; fo/field-options {::m.transactions/account u.pickers/account-picker}
   fo/id            m.n.requests/id
   fo/route-prefix  "edit-request-form"
   fo/title         "Edit Request"})
