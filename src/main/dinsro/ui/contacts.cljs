(ns dinsro.ui.contacts
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/contacts.cljc][Contact Joins]]


(form/defsc-form NewContactForm
  [_this _props]
  {fo/id           m.contacts/id
   fo/attributes   [m.contacts/name
                    m.contacts/pubkey]
   fo/cancel-route ["contacts"]
   fo/route-prefix "new-contact"
   fo/title        "Edit Contact"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewContactForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.contacts/name
                        m.contacts/pubkey]
   ro/controls         {::refresh u.links/refresh-control
                        ::new new-button}
   ro/control-layout   {:action-buttons [::new
                                         ::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "contacts"
   ro/row-actions      []
   ro/row-pk           m.contacts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.contacts/index
   ro/title            "Contacts"})
