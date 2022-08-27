(ns dinsro.ui.core.network-wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.wallets/name]
   ro/controls         {::refresh         u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Network"}}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.wallets/name #(u.links/ui-wallet-link %3)}
   ro/source-attribute ::m.c.wallets/index
   ro/title            "Network Wallets"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys   [report]
          :as        props
          network-id ::m.c.networks/id}]
  {:query             [::m.c.networks/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.networks/id nil
                       :ui/report        {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/starting {:props props})
  (dom/div :.ui.segment
    (if network-id
      (ui-report report)
      (dom/p {} "Network ID not set"))))

(def ui-sub-page (comp/factory SubPage))
