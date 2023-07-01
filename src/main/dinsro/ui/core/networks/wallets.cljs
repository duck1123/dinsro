(ns dinsro.ui.core.networks.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/wallets.cljc]]
;; [[../../../model/core/wallets.cljc]]

(def ident-key ::m.c.networks/id)
(def model-key ::m.c.wallets/id)
(def router-key :dinsro.ui.core.networks/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallets/name #(u.links/ui-wallet-link %3)
                         ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.wallets/name
                         m.c.wallets/user
                         m.c.wallets/derivation]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh         u.links/refresh-control
                         ::m.c.networks/id {:type :uuid :label "Network"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.wallets/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallets/index
   ro/title             "Wallets"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["wallets"]}
  (ui-report report))
