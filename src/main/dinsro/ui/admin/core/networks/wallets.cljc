(ns dinsro.ui.admin.core.networks.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/wallets.cljc]]
;; [[../../../../model/core/wallets.cljc]]

(def index-page-key :admin-core-networks-show-wallets)
(def model-key ::m.c.wallets/id)
(def parent-model-key ::m.c.networks/id)
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
  [_this {::m.c.networks/keys [id]
          :ui/keys            [report]
          :as                 props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.networks/id nil
                       ::m.navlinks/id   index-page-key
                       :ui/report        {}}
   :query             [[::dr/id router-key]
                       ::m.c.networks/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["wallets"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(m.navlinks/defroute :admin-core-networks-show-wallets
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Wallets"
   ::m.navlinks/model-key     ::m.c.wallets/id
   ::m.navlinks/parent-key    :admin-core-networks-show
   ::m.navlinks/router        :admin-core-networks
   ::m.navlinks/required-role :admin})
