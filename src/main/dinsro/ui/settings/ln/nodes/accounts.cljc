(ns dinsro.ui.settings.ln.nodes.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.nodes :as mu.ln.nodes]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-key :settings-ln-nodes-show-accounts)
(def parent-model-key ::m.ln.nodes/id)
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

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))

(m.navlinks/defroute   :settings-ln-nodes-show-accounts
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/model-key     ::m.accounts/id
   ::m.navlinks/parent-key    :settings-ln-nodes-show
   ::m.navlinks/router        :settings-ln-nodes
   ::m.navlinks/required-role :user})
