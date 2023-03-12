(ns dinsro.ui.ln.nodes.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.links/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-accounts!)})

(report/defsc-report Report
  [this props]
  {ro/columns          [m.ln.accounts/wallet
                        m.ln.accounts/address-type
                        m.ln.accounts/node]
   ro/control-layout   {:action-buttons [::fetch ::refresh]
                        :inputs         [[::m.ln.nodes/id]]}
   ro/controls         {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                        ::fetch         fetch-button
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.ln.accounts/wallet #(u.links/ui-wallet-link %2)
                        ::m.ln.accounts/node   #(u.links/ui-node-link %2)}
   ro/source-attribute ::j.ln.accounts/index
   ro/title            "Accounts"
   ro/row-pk           m.ln.accounts/id
   ro/run-on-mount?    true}
  (log/finer :Report/creating {:props props})
  (report/render-layout this))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
