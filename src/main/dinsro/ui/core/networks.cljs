(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]))

(def override-form false)

(form/defsc-form CoreNetworkForm
  [this props]
  {fo/id             m.c.networks/id
   fo/attributes     [m.c.networks/name]
   fo/cancel-route   ["networks"]
   fo/route-prefix   "network"
   fo/title          "Network"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(report/defsc-report CoreNetworksReport
  [_this _props]
  {ro/columns          [m.c.networks/name
                        m.c.networks/chain]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:inputs         [[::tx-id ::search]]
                        :action-buttons [::refresh]}
   ro/form-links       {::m.c.networks/id CoreNetworkForm}
   ro/source-attribute ::m.c.networks/index
   ro/title            "Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true
   ro/route            "networks"})

(def ui-tx-report (comp/factory CoreNetworksReport))
