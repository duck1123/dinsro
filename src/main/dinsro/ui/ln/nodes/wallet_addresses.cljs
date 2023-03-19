(ns dinsro.ui.ln.nodes.wallet-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.wallet-addresses/address
                        m.c.wallet-addresses/wallet
                        m.c.wallet-addresses/path-index]
   ro/control-layout   {:action-buttons [::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.c.wallet-addresses/wallet #(u.links/ui-wallet-link %2)}
   ro/row-actions      [(u.links/subrow-action-button "Generate" ::m.c.wallet-addresses/id ident-key mu.c.wallet-addresses/generate!)]
   ro/row-pk           m.c.wallet-addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.wallet-addresses/index
   ro/title            "Wallet Addresses"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["wallet-addresses"]}
  ((comp/factory Report) report))
