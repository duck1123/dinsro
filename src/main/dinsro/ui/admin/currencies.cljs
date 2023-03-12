(ns dinsro.ui.admin.currencies
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]))

(form/defsc-form NewForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.currencies/name m.currencies/code]
   ro/controls         {::new {:label  "New Currency"
                               :type   :button
                               :action #(form/create! % NewForm)}}
   ro/route            ["currencies"]
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.currencies/index
   ro/title            "Currencies"})
