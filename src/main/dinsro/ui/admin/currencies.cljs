(ns dinsro.ui.admin.currencies
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.links :as u.links]))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/id           m.currencies/id
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(def new-button
  {:label  "New Currency"
   :type   :button
   :action #(form/create! % NewForm)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.currencies/name #(u.links/ui-currency-link %3)}
   ro/columns           [m.currencies/name m.currencies/code
                         j.currencies/source-count
                         j.currencies/rate-count]
   ro/controls          {::new new-button}
   ro/route             ["currencies"]
   ro/row-pk            m.currencies/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.currencies/index
   ro/title             "Currencies"})
