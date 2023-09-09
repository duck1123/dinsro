(ns dinsro.ui.forms.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.mutations.contacts :as mu.contacts]))

(def create-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [`(mu.contacts/create! ~props)])))})

(form/defsc-form NewContactForm
  [_this _props]
  {fo/action-buttons [::create]
   fo/attributes     [m.contacts/name
                      m.contacts/pubkey]
   fo/cancel-route   ["contacts"]
   fo/controls       (merge form/standard-controls {::create create-button})
   fo/id             m.contacts/id
   fo/route-prefix   "new-contact"
   fo/title          "Edit Contact"})
