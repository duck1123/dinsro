(ns dinsro.ui.forms.admin.core.chains
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.chains :as m.c.chains]))

(def override-form false)

(form/defsc-form NewForm
  [this props]
  {fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/id             m.c.chains/id
   fo/route-prefix   "edit-chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (form/render-layout this props))))
