(ns dinsro.ui.core.addresses
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.buttons :as u.buttons]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.c.addresses/address]
   fo/id           m.c.addresses/id
   fo/route-prefix "address"
   fo/title        "Address"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.addresses/address]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "addresses"
   ro/row-actions      [(u.buttons/row-action-button "Fetch" ::m.c.addresses/id mu.c.addresses/delete!)]
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.addresses/index
   ro/title            "Core Addresses"})
