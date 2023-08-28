(ns dinsro.ui.admin.core.wallets.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]))

;; [[../../../../joins/accounts.cljc]]
;; [[../../../../model/accounts.cljc]]

(def index-page-id :admin-core-wallets-show-accounts)
(def model-key ::m.accounts/id)
(def parent-model-key ::m.c.wallets/id)
(def parent-router-id :admin-core-wallets-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.wallets/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/name #(u.links/ui-account-link %3)
                         ::m.accounts/user #(u.links/ui-user-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/user]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::refresh]}
   ro/controls          {::refresh        u.links/refresh-control
                         parent-model-key {:type "uuid"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/admin-index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn [_props]
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(def ui-sub-page (comp/factory SubPage))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Accounts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
