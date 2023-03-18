(ns dinsro.ui.core.network-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.wallets/name
                        m.c.wallets/user
                        m.c.wallets/derivation]
   ro/controls         {::refresh         u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Network"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.wallets/name #(u.links/ui-wallet-link %3)
                        ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Wallets"
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id :dinsro.ui.core.networks/Router]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {:ui/report {}}
   :route-segment     ["wallets"]
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
