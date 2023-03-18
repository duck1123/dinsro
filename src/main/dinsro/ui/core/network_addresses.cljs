(ns dinsro.ui.core.network-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.links :as u.links]))

(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.addresses/address]
   ro/control-layout   {:inputs         [[::m.c.networks/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}

   ro/field-formatters {::m.c.addresses/height #(u.links/ui-block-height-link %3)}
   ro/row-actions      [(u.links/row-action-button "Fetch" ::m.c.addresses/id mu.c.addresses/fetch!)]
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.addresses/index-by-network
   ro/title            "Addresses"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [:component/id ::SubPage])
   :initial-state {:ui/report {}}
   :query         [[::dr/id router-key]
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["addresses"]}
  ((comp/factory Report) report))
