(ns dinsro.ui.forms.admin.core.addresses
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.addresses :as m.c.addresses]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.c.addresses/address]
   fo/id           m.c.addresses/id
   fo/route-prefix "address"
   fo/title        "Address"})
