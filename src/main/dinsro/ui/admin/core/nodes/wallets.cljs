(ns dinsro.ui.admin.core.nodes.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/wallets.cljc]]
;; [[../../../../model/core/wallets.cljc]]

(def index-page-key :admin-core-nodes-show-wallets)
(def model-key ::m.c.wallets/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                         ::m.c.wallets/name #(u.links/ui-wallet-link %3)
                         ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.wallets/name
                         m.c.wallets/derivation
                         m.c.wallets/key
                         m.c.wallets/user
                         m.c.wallets/network]
   ro/control-layout    {:inputs         [[::m.c.nodes/id]]
                         :action-buttons [::new ::refresh]}
   ro/controls          {::new          u.c.wallets/new-action-button
                         ::m.c.nodes/id {:type :uuid :label "Nodes"}
                         ::refresh      u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "wallets"
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.c.wallets/delete!)]
   ro/row-pk            m.c.wallets/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallets/index
   ro/title             "Wallets"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.c.nodes/keys [id]
          :ui/keys         [report]
          :as              props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.nodes/id  nil
                       ::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.c.nodes/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
