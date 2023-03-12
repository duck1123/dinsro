(ns dinsro.ui.core.networks.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.c.networks/id)
(def router-key :dinsro.ui.core.networks/Router)

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
   ro/source-attribute ::j.c.wallets/index
   ro/title            "Wallets"
   ro/row-pk           m.c.wallets/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :initial-state     {:ui/report {}}
   :route-segment     ["wallets"]
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
