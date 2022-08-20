(ns dinsro.ui.core.chains
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.ui.links :as u.links]))

(def override-form false)

(form/defsc-form CoreChainForm
  [this props]
  {fo/id             m.c.chains/id
   fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/route-prefix   "chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.chains/name]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.c.chains/index
   ro/title            "Chains"
   ro/row-pk           m.c.chains/id
   ro/run-on-mount?    true
   ro/route            "chains"})

(def ui-tx-report (comp/factory Report))
