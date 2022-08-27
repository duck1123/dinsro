(ns dinsro.ui.ln.accounts
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.accounts/id
                        m.ln.accounts/node]
   ro/field-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/route            "accounts"
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.accounts/index
   ro/title            "Lightning Accounts Report"}
  (dom/div {}
    (dom/h1 {} "Accounts")
    (report/render-layout this)))
