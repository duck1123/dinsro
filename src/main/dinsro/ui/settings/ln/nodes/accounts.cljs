(ns dinsro.ui.settings.ln.nodes.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.ln.nodes/id)
(def router-key :dinsro.ui.ln.nodes/Router)

(def fetch-button
  {:type   :button
   :label  "Fetch"
   :action (u.buttons/report-action ::m.ln.nodes/id mu.ln.nodes/fetch-accounts!)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.accounts/wallet #(u.links/ui-wallet-link %2)
                         ::m.ln.accounts/node   #(u.links/ui-node-link %2)}
   ro/columns           [m.ln.accounts/wallet
                         m.ln.accounts/address-type
                         m.ln.accounts/node]
   ro/control-layout    {:action-buttons [::fetch ::refresh]
                         :inputs         [[::m.ln.nodes/id]]}
   ro/controls          {::m.ln.nodes/id {:type :uuid :label "Nodes"}
                         ::fetch         fetch-button
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.accounts/index
   ro/title             "Accounts"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]}
  ((comp/factory Report) report))
