(ns dinsro.ui.forms.currencies
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.currencies :as m.currencies]))

;; [[../actions/currencies.clj]]
;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/id           m.currencies/id
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})
