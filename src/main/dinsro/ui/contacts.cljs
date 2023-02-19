(ns dinsro.ui.contacts
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/contacts.cljc][Contact Joins]]

(report/defsc-report Report
  [_this _props]
  {ro/columns          []
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "contacts"
   ro/row-actions      []
   ro/row-pk           m.contacts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.contacts/index
   ro/title            "Contacts"})
