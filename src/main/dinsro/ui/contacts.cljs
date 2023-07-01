(ns dinsro.ui.contacts
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/contacts.cljc]]
;; [[../model/contacts.cljc]]

(def model-key ::m.contacts/id)

(form/defsc-form NewContactForm
  [_this _props]
  {fo/attributes   [m.contacts/name
                    m.contacts/pubkey]
   fo/cancel-route ["contacts"]
   fo/id           m.contacts/id
   fo/route-prefix "new-contact"
   fo/title        "Edit Contact"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewContactForm))})

(report/defsc-report Report
  [this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.contacts/name
                         m.contacts/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control
                         ::new     new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "contacts"
   ro/row-pk            m.contacts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.contacts/index
   ro/title             "Contacts"}
  (dom/div :.ui.container.centered
    (report/render-layout this)))
